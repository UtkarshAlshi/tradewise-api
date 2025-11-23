package com.tradewise.api.controller;

import com.tradewise.api.dto.BacktestRequest;
import com.tradewise.api.dto.response.BacktestReportResponse;
import com.tradewise.api.service.BacktestingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // <-- ADD
import org.springframework.security.core.userdetails.UserDetails; // <-- ADD

@RestController
@RequestMapping("/api/backtest")
public class BacktestingController {

    private final BacktestingService backtestingService;

    public BacktestingController(BacktestingService backtestingService) {
        this.backtestingService = backtestingService;
    }

    @PostMapping
    public ResponseEntity<BacktestReportResponse> runBacktest(
            @Valid @RequestBody BacktestRequest request,
            @AuthenticationPrincipal UserDetails userDetails){

        try {
            BacktestReportResponse report = backtestingService.runBacktest(request, userDetails.getUsername()); // <-- ADD
             return ResponseEntity.ok(report);
        } catch (Exception e) {
            // This is a complex operation, so we'll catch general errors
            // In a real app, we'd have more specific exceptions
            return ResponseEntity.badRequest().body(
                    BacktestReportResponse.builder()
                            .strategyName("Error")
                            .symbol(e.getMessage()) // Use symbol field to pass error
                            .build()
            );
        }
    }
}