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
}