package com.tradewise.api.controller;

import com.tradewise.api.dto.response.StrategyResponse;
import com.tradewise.api.dto.strategy.CreateStrategyRequest;
import com.tradewise.api.model.Strategy;
import com.tradewise.api.service.StrategyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
@RestController
@RequestMapping("/api/strategies")
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @PostMapping
    public ResponseEntity<StrategyResponse> createStrategy(
            @Valid @RequestBody CreateStrategyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Strategy savedStrategy = strategyService.createStrategy(request, userEmail);

        // Map to our safe response DTO
        StrategyResponse response = new StrategyResponse(
                savedStrategy.getId(),
                savedStrategy.getName(),
                savedStrategy.getDescription(),
                savedStrategy.getCreatedAt(),
                savedStrategy.getUser().getId()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // We will add GET endpoints here next

    @GetMapping
    public ResponseEntity<List<StrategyResponse>> getStrategies(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        List<StrategyResponse> strategies = strategyService.getStrategiesByUser(userEmail);

        return ResponseEntity.ok(strategies);
    }
}