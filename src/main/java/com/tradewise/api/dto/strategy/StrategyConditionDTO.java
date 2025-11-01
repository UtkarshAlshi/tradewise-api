package com.tradewise.api.dto.strategy;

import lombok.Data;
import java.util.Map;

@Data
public class StrategyConditionDTO {
    private String indicatorA;
    private Map<String, Object> indicatorAParams;
    private String operator;
    private String indicatorBType;
    private String indicatorBValue;
    private Map<String, Object> indicatorBParams;
}