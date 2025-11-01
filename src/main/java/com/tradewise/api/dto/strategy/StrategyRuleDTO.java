package com.tradewise.api.dto.strategy;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class StrategyRuleDTO {
    private String action;
    private BigDecimal actionAmountPercent;
    private int priority;
    private Set<StrategyConditionDTO> conditions;
}