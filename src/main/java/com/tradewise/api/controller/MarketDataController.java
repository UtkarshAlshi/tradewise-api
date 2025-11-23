package com.tradewise.api.controller;

import com.tradewise.api.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /**
     * GET /api/market-data/{symbol}
     * Fetches the latest price for a given stock symbol (e.g., AAPL).
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<String> getMarketData(@PathVariable String symbol) {

        Optional<BigDecimal> price = marketDataService.getLatestPrice(symbol.toUpperCase());

        if (price.isPresent()) {
            return ResponseEntity.ok(price.get().toString());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}