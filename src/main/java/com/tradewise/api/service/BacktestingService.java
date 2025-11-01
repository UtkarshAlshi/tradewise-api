package com.tradewise.api.service;

import com.tradewise.api.dto.BacktestRequest;
import com.tradewise.api.dto.response.BacktestReportResponse;
import com.tradewise.api.model.Strategy;
import com.tradewise.api.model.StrategyCondition;
import com.tradewise.api.model.StrategyRule;
import com.tradewise.api.repository.StrategyRepository;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.analysis.CashFlow;

// --- CORRECTED IMPORTS (v0.17) ---
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.MaximumDrawdownCriterion;
import org.ta4j.core.criteria.NumberOfPositionsCriterion;
import org.ta4j.core.criteria.PositionsRatioCriterion;
import org.ta4j.core.AnalysisCriterion.PositionFilter;

import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;

// --- MISSING IMPORTS ADDED (from your screenshots) ---
import org.ta4j.core.rules.*;
import org.ta4j.core.Trade; // Replaces 'Order'
// --- END IMPORT FIXES ---

import com.tradewise.api.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class BacktestingService {

    private final StrategyRepository strategyRepository;
    private final MarketDataService marketDataService;
    private final UserService userService;

    public BacktestingService(StrategyRepository strategyRepository,
                              MarketDataService marketDataService,
                              UserService userService) {
        this.strategyRepository = strategyRepository;
        this.marketDataService = marketDataService;
        this.userService = userService;
    }

    /**
     * Main method to run the backtest.
     */
    public BacktestReportResponse runBacktest(BacktestRequest request, String userEmail) {
        // 1. Fetch Strategy and Historical Data
        User user = userService.findByEmail(userEmail);
        Strategy strategy = strategyRepository.findById(request.getStrategyId())
                .orElseThrow(() -> new RuntimeException("Strategy not found"));

        // --- CRITICAL SECURITY CHECK ---
        if (!strategy.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access Denied: You do not own this strategy.");
        }

        BarSeries barSeries = marketDataService.getHistoricalData(
                request.getSymbol(),
                request.getStartDate(),
                request.getEndDate()
        );

        // 2. Build ta4j Strategy from our DB entities
        BaseStrategy ta4jStrategy = buildTa4jStrategy(strategy, barSeries);

        // 3. Run the Simulation
        BarSeriesManager manager = new BarSeriesManager(barSeries);
        TradingRecord tradingRecord = manager.run(
                ta4jStrategy,
                Trade.TradeType.BUY, // <-- FIX: Renamed from Order.OrderType
                DecimalNum.valueOf(request.getInitialCash())
        );

        // 4. Calculate Metrics
        return calculateReport(strategy.getName(), request.getSymbol(), barSeries, tradingRecord, request.getInitialCash());
    }

    /**
     * Translates our DB Strategy into a ta4j-compatible Strategy.
     */
    private BaseStrategy buildTa4jStrategy(Strategy dbStrategy, BarSeries barSeries) {
        Map<String, Indicator> indicatorCache = new HashMap<>();

        Rule entryRule = new BooleanRule(false); // <-- FIX: Import was missing
        Rule exitRule = new BooleanRule(false);  // <-- FIX: Import was missing

        for (StrategyRule dbRule : dbStrategy.getRules()) {
            Rule combinedRuleForConditions = new BooleanRule(true); // <-- FIX: Import was missing

            if (dbRule.getConditions() == null) continue;

            for (StrategyCondition dbCondition : dbRule.getConditions()) {
                Rule conditionRule = buildTa4jRule(dbCondition, barSeries, indicatorCache);
                combinedRuleForConditions = combinedRuleForConditions.and(conditionRule);
            }

            if ("BUY".equalsIgnoreCase(dbRule.getAction())) {
                entryRule = entryRule.or(combinedRuleForConditions);
            } else if ("SELL".equalsIgnoreCase(dbRule.getAction())) {
                exitRule = exitRule.or(combinedRuleForConditions);
            }
        }

        return new BaseStrategy(entryRule, new StopGainRule(new ClosePriceIndicator(barSeries), DecimalNum.valueOf(0)).or(exitRule).or(new WaitForRule((Trade.TradeType) exitRule, 1)));
    }

    /**
     * Helper to build a single ta4j Rule from our DB Condition.
     */
    private Rule buildTa4jRule(StrategyCondition condition, BarSeries series, Map<String, Indicator> cache) {
        Indicator indicatorA = getIndicator(condition.getIndicatorA(), condition.getIndicatorAParams(), series, cache);

        if ("VALUE".equalsIgnoreCase(condition.getIndicatorBType())) {
            DecimalNum valueB = DecimalNum.valueOf(condition.getIndicatorBValue());

            switch (condition.getOperator().toUpperCase()) {
                case "GREATER_THAN":
                    return new OverIndicatorRule(indicatorA, valueB);
                case "LESS_THAN":
                    return new UnderIndicatorRule(indicatorA, valueB);
                default:
                    throw new RuntimeException("Unsupported operator: " + condition.getOperator());
            }
        }
        else if ("INDICATOR".equalsIgnoreCase(condition.getIndicatorBType())) {
            Indicator indicatorB = getIndicator(condition.getIndicatorBValue(), condition.getIndicatorBParams(), series, cache);

            switch (condition.getOperator().toUpperCase()) {
                case "GREATER_THAN":
                    return new OverIndicatorRule(indicatorA, indicatorB);
                case "LESS_THAN":
                    return new UnderIndicatorRule(indicatorA, indicatorB);
                case "CROSSES_ABOVE":
                    return new CrossedUpIndicatorRule(indicatorA, indicatorB); // <-- FIX: Import was missing
                case "CROSSES_BELOW":
                    return new CrossedDownIndicatorRule(indicatorA, indicatorB); // <-- FIX: Import was missing
                default:
                    throw new RuntimeException("Unsupported operator: " + condition.getOperator());
            }
        }
        throw new RuntimeException("Unsupported IndicatorBType: " + condition.getIndicatorBType());
    }

    /**
     * Helper to create (or get from cache) a ta4j Indicator.
     */
    private Indicator getIndicator(String name, Map<String, Object> params, BarSeries series, Map<String, Indicator> cache) {
        String cacheKey = name + (params != null ? params.toString() : "{}");
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        Indicator indicator;
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        switch (name.toUpperCase()) {
            case "PRICE":
                indicator = closePrice;
                break;
            case "SMA":
                int smaPeriod = ((Number) params.get("period")).intValue();
                indicator = new SMAIndicator(closePrice, smaPeriod);
                break;
            case "EMA":
                int emaPeriod = ((Number) params.get("period")).intValue();
                indicator = new EMAIndicator(closePrice, emaPeriod);
                break;
            case "RSI":
                int rsiPeriod = ((Number) params.get("period")).intValue();
                indicator = new RSIIndicator(closePrice, rsiPeriod);
                break;
            default:
                throw new RuntimeException("Unsupported indicator: " + name);
        }

        cache.put(cacheKey, indicator);
        return indicator;
    }

    /**
     * Calculates final report metrics from the trading record.
     */
    private BacktestReportResponse calculateReport(String strategyName, String symbol, BarSeries series, TradingRecord record, Double initialCash) {
        if (record.getTrades().isEmpty()) {
            return BacktestReportResponse.builder()
                    .strategyName(strategyName).symbol(symbol).totalTrades(0)
                    .totalProfitLoss(BigDecimal.ZERO).totalReturnPercent(BigDecimal.ZERO)
                    .winRatePercent(BigDecimal.ZERO).maxDrawdownPercent(BigDecimal.ZERO)
                    .build();
        }

        CashFlow cashFlow = new CashFlow(series, record, DecimalNum.valueOf(initialCash).intValue());

        // --- FIX: Cast getDelegate() to BigDecimal ---
        BigDecimal finalValue = (BigDecimal) cashFlow.getValue(series.getEndIndex()).getDelegate();
        BigDecimal pnl = finalValue.subtract(BigDecimal.valueOf(initialCash));
        BigDecimal returnPercent = pnl.divide(BigDecimal.valueOf(initialCash), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));


        // --- FIX: Correct class names and types (v0.17) ---
        int totalTrades = new NumberOfPositionsCriterion().calculate(series, record).intValue(); // <-- FIX: .intValue()

        BigDecimal winRate = ((BigDecimal) new PositionsRatioCriterion(PositionFilter.PROFIT) // <-- FIX: Added constructor arg & import
                .calculate(series, record)
                .getDelegate()) // <-- FIX: Cast to BigDecimal
                .multiply(BigDecimal.valueOf(100));

        BigDecimal maxDrawdown = ((BigDecimal) new MaximumDrawdownCriterion().calculate(series, record)
                .getDelegate()) // <-- FIX: Cast to BigDecimal
                .multiply(BigDecimal.valueOf(100));
        // --- END OF FIX ---

        return BacktestReportResponse.builder()
                .strategyName(strategyName)
                .symbol(symbol)
                .totalTrades(totalTrades)
                .totalProfitLoss(pnl.setScale(2, RoundingMode.HALF_UP))
                .totalReturnPercent(returnPercent.setScale(2, RoundingMode.HALF_UP))
                .winRatePercent(winRate.setScale(2, RoundingMode.HALF_UP))
                .maxDrawdownPercent(maxDrawdown.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}