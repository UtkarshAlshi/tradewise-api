package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetAnalyticsResponse {
    private UUID assetId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal purchasePrice; // Avg. purchase price
    private BigDecimal totalCost; // quantity * purchasePrice
    private BigDecimal currentPrice;
    private BigDecimal marketValue; // quantity * currentPrice
    private BigDecimal gainLoss; // marketValue - totalCost
    private BigDecimal gainLossPercent;
}