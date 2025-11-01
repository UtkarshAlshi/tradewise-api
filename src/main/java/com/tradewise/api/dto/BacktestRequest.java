package com.tradewise.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BacktestRequest {

    @NotNull(message = "Strategy ID is required")
    private UUID strategyId;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Initial cash is required")
    private Double initialCash;
}