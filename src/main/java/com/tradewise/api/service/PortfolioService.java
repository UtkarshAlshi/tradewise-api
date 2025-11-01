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

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService; // We'll use this to find the user

    public PortfolioService(PortfolioRepository portfolioRepository, UserService userService) {
        this.portfolioRepository = portfolioRepository;
        this.userService = userService;
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
}