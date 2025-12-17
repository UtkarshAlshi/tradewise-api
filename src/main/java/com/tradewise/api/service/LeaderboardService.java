package com.tradewise.api.service;

import com.tradewise.api.dto.response.LeaderboardEntryResponse;
// import com.tradewise.api.dto.response.PortfolioAnalyticsResponse; // No longer needed directly here
// import com.tradewise.api.model.Portfolio; // No longer needed directly here
// import com.tradewise.api.repository.PortfolioRepository; // No longer needed directly here
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    // These dependencies have been moved to portfolio-service
    // private final PortfolioRepository portfolioRepository;
    // private final PortfolioService portfolioService;

    // public LeaderboardService(PortfolioRepository portfolioRepository, PortfolioService portfolioService) {
    //     this.portfolioRepository = portfolioRepository;
    //     this.portfolioService = portfolioService;
    // }

    public List<LeaderboardEntryResponse> getLeaderboard() {
        // This method's logic needs to be re-implemented to call the portfolio-service
        // via a REST client. For now, we return an empty list to allow compilation.
        return new ArrayList<>();

        /*
        // 1. Fetch all portfolios (this includes user data)
        List<Portfolio> allPortfolios = portfolioRepository.findAll();

        List<LeaderboardEntryResponse> rankedEntries = new ArrayList<>();

        // 2. Calculate analytics for each portfolio
        //    (This is the very slow part)
        for (Portfolio portfolio : allPortfolios) {
            try {
                PortfolioAnalyticsResponse analytics = portfolioService.getPortfolioAnalytics(
                        portfolio.getId(),
                        portfolio.getUser().getEmail()
                );

                // Only add portfolios with a valid gain/loss
                if (analytics.getTotalGainLossPercent() != null &&
                        analytics.getTotalGainLossPercent().compareTo(BigDecimal.ZERO) != 0) {

                    rankedEntries.add(new LeaderboardEntryResponse(
                            0, // Rank will be set after sorting
                            analytics.getPortfolioName(),
                            portfolio.getUser().getEmail(),
                            analytics.getTotalGainLossPercent()
                    ));
                }
            } catch (Exception e) {
                // Log the error for this specific portfolio and continue
                System.err.println("Failed to calculate analytics for portfolio " + portfolio.getId() + ": " + e.getMessage());
            }
        }

        // 3. Sort the list by gain/loss % in descending order
        rankedEntries.sort(Comparator.comparing(LeaderboardEntryResponse::getTotalGainLossPercent).reversed());

        // 4. Set the rank and return the top 10
        List<LeaderboardEntryResponse> topEntries = new ArrayList<>();
        for (int i = 0; i < Math.min(10, rankedEntries.size()); i++) {
            LeaderboardEntryResponse entry = rankedEntries.get(i);
            entry.setRank(i + 1);
            topEntries.add(entry);
        }

        return topEntries;
        */
    }
}
