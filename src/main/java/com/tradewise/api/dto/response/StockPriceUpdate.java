package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StockPriceUpdate {
    private String symbol;
    private BigDecimal price;
}