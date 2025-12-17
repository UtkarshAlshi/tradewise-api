package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // Added NoArgsConstructor for completeness
import java.math.BigDecimal;

@Data
// @AllArgsConstructor // Removed to explicitly define constructor
@NoArgsConstructor // Keep NoArgsConstructor if needed elsewhere
public class StockPriceUpdate {
    private String symbol;
    private BigDecimal price;

    // Explicitly define AllArgsConstructor
    public StockPriceUpdate(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }
}
