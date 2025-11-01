package com.tradewise.api.service;

import com.tradewise.api.dto.CreatePortfolioRequest;
import com.tradewise.api.model.Portfolio;
import com.tradewise.api.model.User;
import com.tradewise.api.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tradewise.api.dto.response.PortfolioResponse;
import java.util.List;
import java.util.stream.Collectors;
import com.tradewise.api.dto.AddAssetRequest;
import com.tradewise.api.model.PortfolioAsset;
import com.tradewise.api.repository.PortfolioAssetRepository;
import java.util.UUID;
import com.tradewise.api.dto.response.PortfolioAssetResponse;
import java.util.List; // <-- ADD
import java.util.stream.Collectors; // <-- ADD
import com.tradewise.api.dto.response.AssetAnalyticsResponse; // <-- ADD
import com.tradewise.api.dto.response.PortfolioAnalyticsResponse; // <-- ADD
import java.math.BigDecimal; // <-- ADD
import java.math.RoundingMode; // <-- ADD
import java.util.ArrayList; // <-- ADD
import java.util.HashMap; // <-- ADD
import java.util.Map; // <-- ADD
import java.util.Set; // <-- ADD
import java.util.stream.Collectors; // <-- ADD

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService; // We'll use this to find the user
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final MarketDataService marketDataService; // <-- ADD THIS

    public PortfolioService(PortfolioRepository portfolioRepository,
                            UserService userService,
                            PortfolioAssetRepository portfolioAssetRepository,
                            MarketDataService marketDataService) { // <-- ADD THIS
        this.portfolioRepository = portfolioRepository;
        this.userService = userService;
        this.portfolioAssetRepository = portfolioAssetRepository;
        this.marketDataService = marketDataService; // <-- ADD THIS
    }

    @Transactional
    public Portfolio createPortfolio(CreatePortfolioRequest request, String userEmail) {
        // 1. Find the currently logged-in user
        User currentUser = userService.findByEmail(userEmail);

        // 2. Build the new Portfolio entity
        Portfolio portfolio = Portfolio.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(currentUser) // Link it to the user
                .build();

        // 3. Save it to the database
        return portfolioRepository.save(portfolio);
    }

    @Transactional(readOnly = true) // readOnly = true is an optimization for GET operations
    public List<PortfolioResponse> getPortfoliosByUser(String userEmail) {
        // 1. Find the user
        User currentUser = userService.findByEmail(userEmail);

        // 2. Find all portfolios for that user
        List<Portfolio> portfolios = portfolioRepository.findByUserId(currentUser.getId());

        // 3. Map the list of Portfolio entities to a list of PortfolioResponse DTOs
        return portfolios.stream()
                .map(portfolio -> new PortfolioResponse(
                        portfolio.getId(),
                        portfolio.getName(),
                        portfolio.getDescription(),
                        portfolio.getCreatedAt(),
                        portfolio.getUser().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public PortfolioAsset addAssetToPortfolio(UUID portfolioId, AddAssetRequest request, String userEmail) {

        // 1. Find the portfolio
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        // 2. Find the user
        User currentUser = userService.findByEmail(userEmail);

        // 3. --- CRITICAL SECURITY CHECK ---
        //    Ensure the portfolio belongs to the currently authenticated user.
        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
            // In a real app, this would be a more specific AccessDeniedException
        }

        // 4. Build the new asset
        PortfolioAsset newAsset = PortfolioAsset.builder()
                .portfolio(portfolio) // Link it to the parent portfolio
                .symbol(request.getSymbol().toUpperCase())
                .quantity(request.getQuantity())
                .purchasePrice(request.getPurchasePrice())
                .purchaseDate(request.getPurchaseDate())
                .build();

        // 5. Save the asset to the database
        return portfolioAssetRepository.save(newAsset);
    }

    @Transactional(readOnly = true) // Optimization for GET requests
    public List<PortfolioAssetResponse> getAssetsForPortfolio(UUID portfolioId, String userEmail) {

        // 1. Find the portfolio
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        // 2. Find the user
        User currentUser = userService.findByEmail(userEmail);

        // 3. --- CRITICAL SECURITY CHECK ---
        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
        }

        // 4. Fetch the assets
        List<PortfolioAsset> assets = portfolioAssetRepository.findAllByPortfolioId(portfolioId);

        // 5. Map to DTOs
        return assets.stream()
                .map(asset -> new PortfolioAssetResponse(
                        asset.getId(),
                        asset.getSymbol(),
                        asset.getQuantity(),
                        asset.getPurchasePrice(),
                        asset.getPurchaseDate(),
                        asset.getPortfolio().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAssetFromPortfolio(UUID portfolioId, UUID assetId, String userEmail) {

        // 1. Find the user
        User currentUser = userService.findByEmail(userEmail);

        // 2. Find the portfolio
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        // 3. Find the asset
        PortfolioAsset asset = portfolioAssetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        // 4. --- CRITICAL SECURITY CHECK ---
        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
        }

        // 5. --- CRITICAL INTEGRITY CHECK ---
        if (!asset.getPortfolio().getId().equals(portfolioId)) {
            throw new RuntimeException("Access Denied: Asset does not belong to this portfolio.");
        }

        // 6. Both checks passed, delete the asset
        portfolioAssetRepository.delete(asset);
    }

    @Transactional(readOnly = true)
    public PortfolioAnalyticsResponse getPortfolioAnalytics(UUID portfolioId, String userEmail) {

        // 1. Security Check (User + Portfolio)
        User currentUser = userService.findByEmail(userEmail);
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + portfolioId));

        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access Denied: You do not own this portfolio.");
        }

        // 2. Get all assets
        List<PortfolioAsset> assets = portfolioAssetRepository.findAllByPortfolioId(portfolioId);

        // 3. Get unique symbols
        Set<String> uniqueSymbols = assets.stream()
                .map(PortfolioAsset::getSymbol)
                .collect(Collectors.toSet());

        // 4. Fetch current prices for all unique symbols
        //    WARNING: This can be slow and hit rate limits.
        Map<String, BigDecimal> currentPrices = new HashMap<>();
        for (String symbol : uniqueSymbols) {
            // Default to 0 if API fails
            BigDecimal price = marketDataService.getLatestPrice(symbol).orElse(BigDecimal.ZERO);
            currentPrices.put(symbol, price);
        }

        // 5. Calculate analytics
        BigDecimal portfolioTotalValue = BigDecimal.ZERO;
        BigDecimal portfolioTotalPurchaseCost = BigDecimal.ZERO;
        List<AssetAnalyticsResponse> assetAnalyticsList = new ArrayList<>();

        for (PortfolioAsset asset : assets) {
            BigDecimal totalCost = asset.getQuantity().multiply(asset.getPurchasePrice());
            BigDecimal currentPrice = currentPrices.getOrDefault(asset.getSymbol(), BigDecimal.ZERO);
            BigDecimal marketValue = asset.getQuantity().multiply(currentPrice);
            BigDecimal gainLoss = marketValue.subtract(totalCost);

            BigDecimal gainLossPercent = BigDecimal.ZERO;
            if (totalCost.compareTo(BigDecimal.ZERO) != 0) { // Avoid division by zero
                gainLossPercent = gainLoss.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            }

            assetAnalyticsList.add(new AssetAnalyticsResponse(
                    asset.getId(),
                    asset.getSymbol(),
                    asset.getQuantity(),
                    asset.getPurchasePrice(),
                    totalCost,
                    currentPrice,
                    marketValue,
                    gainLoss,
                    gainLossPercent
            ));

            portfolioTotalValue = portfolioTotalValue.add(marketValue);
            portfolioTotalPurchaseCost = portfolioTotalPurchaseCost.add(totalCost);
        }

        // 6. Calculate portfolio-level analytics
        BigDecimal portfolioTotalGainLoss = portfolioTotalValue.subtract(portfolioTotalPurchaseCost);
        BigDecimal portfolioTotalGainLossPercent = BigDecimal.ZERO;
        if (portfolioTotalPurchaseCost.compareTo(BigDecimal.ZERO) != 0) { // Avoid division by zero
            portfolioTotalGainLossPercent = portfolioTotalGainLoss
                    .divide(portfolioTotalPurchaseCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // 7. Build final response
        return new PortfolioAnalyticsResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolioTotalValue,
                portfolioTotalPurchaseCost,
                portfolioTotalGainLoss,
                portfolioTotalGainLossPercent,
                assetAnalyticsList
        );
    }
}