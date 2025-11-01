package com.tradewise.api.dto.strategy;

import lombok.Data;
import java.util.Set;

@Data
public class CreateStrategyRequest {
    private String name;
    private String description;
    private Set<StrategyRuleDTO> rules;
}