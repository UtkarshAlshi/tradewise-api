package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {

    private int rank;
    private String portfolioName;
    private String userEmail; // Anonymize this later if needed
    private BigDecimal totalGainLossPercent;
}