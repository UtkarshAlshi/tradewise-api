package com.tradewise.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyResponse {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private UUID userId;
}