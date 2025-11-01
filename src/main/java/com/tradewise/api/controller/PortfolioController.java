package com.tradewise.api.controller;

import com.tradewise.api.dto.CreatePortfolioRequest;
import com.tradewise.api.dto.response.PortfolioResponse;
import com.tradewise.api.model.Portfolio;
import com.tradewise.api.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.tradewise.api.dto.AddAssetRequest;
import com.tradewise.api.dto.response.PortfolioAssetResponse;
import com.tradewise.api.model.PortfolioAsset;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.tradewise.api.dto.response.PortfolioAnalyticsResponse; // <-- ADD

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Get the email of the logged-in user
        String userEmail = userDetails.getUsername();

        // Call the service to create the portfolio
        Portfolio newPortfolio = portfolioService.createPortfolio(request, userEmail);

        // Map the entity to our safe DTO
        PortfolioResponse response = new PortfolioResponse(
                newPortfolio.getId(),
                newPortfolio.getName(),
                newPortfolio.getDescription(),
                newPortfolio.getCreatedAt(),
                newPortfolio.getUser().getId()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // We will add endpoints here for GET, PUT, and DELETE

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getPortfolios(
            @AuthenticationPrincipal UserDetails userDetails) {

        // Get the email of the logged-in user
        String userEmail = userDetails.getUsername();

        // Call the service to get the list
        List<PortfolioResponse> portfolios = portfolioService.getPortfoliosByUser(userEmail);

        // Return the list with a 200 OK
        return ResponseEntity.ok(portfolios);
    }

    @PostMapping("/{portfolioId}/assets")
    public ResponseEntity<PortfolioAssetResponse> addAssetToPortfolio(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody AddAssetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        // Call the service
        PortfolioAsset savedAsset = portfolioService.addAssetToPortfolio(portfolioId, request, userEmail);

        // Map to the response DTO
        PortfolioAssetResponse response = new PortfolioAssetResponse(
                savedAsset.getId(),
                savedAsset.getSymbol(),
                savedAsset.getQuantity(),
                savedAsset.getPurchasePrice(),
                savedAsset.getPurchaseDate(),
                savedAsset.getPortfolio().getId()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{portfolioId}/assets")
    public ResponseEntity<List<PortfolioAssetResponse>> getAssetsForPortfolio(
            @PathVariable UUID portfolioId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        List<PortfolioAssetResponse> response = portfolioService.getAssetsForPortfolio(portfolioId, userEmail);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{portfolioId}/assets/{assetId}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable UUID portfolioId,
            @PathVariable UUID assetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        portfolioService.deleteAssetFromPortfolio(portfolioId, assetId, userEmail);

        // A 204 No Content response is standard for a successful DELETE
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/analytics")
    public ResponseEntity<PortfolioAnalyticsResponse> getPortfolioAnalytics(
            @PathVariable UUID portfolioId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        PortfolioAnalyticsResponse response = portfolioService.getPortfolioAnalytics(portfolioId, userEmail);

        return ResponseEntity.ok(response);
    }
}