package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAnalyticsResponse {
    private UUID portfolioId;
    private String portfolioName;
    private BigDecimal totalValue; // Current total market value
    private BigDecimal totalPurchaseCost; // Total cost of all assets
    private BigDecimal totalGainLoss; // totalValue - totalPurchaseCost
    private BigDecimal totalGainLossPercent;
    private List<AssetAnalyticsResponse> assets; // List of individual assets
}