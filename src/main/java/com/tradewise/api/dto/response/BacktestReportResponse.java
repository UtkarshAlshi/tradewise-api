package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacktestReportResponse {
    private String strategyName;
    private String symbol;
    private int totalTrades;
    private BigDecimal totalProfitLoss;
    private BigDecimal totalReturnPercent;
    private BigDecimal winRatePercent;
    private BigDecimal maxDrawdownPercent;
}