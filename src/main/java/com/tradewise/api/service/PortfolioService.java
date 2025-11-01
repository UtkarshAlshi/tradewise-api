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
import com.tradewise.api.dto.response.PortfolioAssetResponse; // <-- ADD
import java.util.List; // <-- ADD
import java.util.stream.Collectors; // <-- ADD

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService; // We'll use this to find the user
    private final PortfolioAssetRepository portfolioAssetRepository;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            UserService userService,
                            PortfolioAssetRepository portfolioAssetRepository) { // <-- ADD THIS
        this.portfolioRepository = portfolioRepository;
        this.userService = userService;
        this.portfolioAssetRepository = portfolioAssetRepository; // <-- ADD THIS
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
}