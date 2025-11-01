package com.tradewise.api.service;

import com.tradewise.api.dto.CreatePortfolioRequest;
import com.tradewise.api.model.Portfolio;
import com.tradewise.api.model.User;
import com.tradewise.api.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}