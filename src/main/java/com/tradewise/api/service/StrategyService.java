package com.tradewise.api.service;

import com.tradewise.api.dto.strategy.CreateStrategyRequest;
import com.tradewise.api.dto.strategy.StrategyConditionDTO;
import com.tradewise.api.dto.strategy.StrategyRuleDTO;
import com.tradewise.api.model.*;
import com.tradewise.api.repository.StrategyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final UserService userService;

    public StrategyService(StrategyRepository strategyRepository, UserService userService) {
        this.strategyRepository = strategyRepository;
        this.userService = userService;
    }

    @Transactional
    public Strategy createStrategy(CreateStrategyRequest request, String userEmail) {
        User user = userService.findByEmail(userEmail);

        // 1. Create the parent Strategy
        Strategy strategy = new Strategy();
        strategy.setUser(user);
        strategy.setName(request.getName());
        strategy.setDescription(request.getDescription());

        Set<StrategyRule> rules = new HashSet<>();
        for (StrategyRuleDTO ruleDTO : request.getRules()) {

            // 2. Create the StrategyRule
            StrategyRule rule = new StrategyRule();
            rule.setStrategy(strategy); // Link to parent
            rule.setAction(ruleDTO.getAction());
            rule.setActionAmountPercent(ruleDTO.getActionAmountPercent());
            rule.setPriority(ruleDTO.getPriority());

            Set<StrategyCondition> conditions = new HashSet<>();
            for (StrategyConditionDTO condDTO : ruleDTO.getConditions()) {

                // 3. Create the StrategyCondition
                StrategyCondition condition = new StrategyCondition();
                condition.setRule(rule); // Link to parent
                condition.setIndicatorA(condDTO.getIndicatorA());
                condition.setIndicatorAParams(condDTO.getIndicatorAParams());
                condition.setOperator(condDTO.getOperator());
                condition.setIndicatorBType(condDTO.getIndicatorBType());
                condition.setIndicatorBValue(condDTO.getIndicatorBValue());
                condition.setIndicatorBParams(condDTO.getIndicatorBParams());

                conditions.add(condition);
            }
            rule.setConditions(conditions);
            rules.add(rule);
        }
        strategy.setRules(rules);

        // 4. Save the parent. CascadeType.ALL will save all rules and conditions.
        return strategyRepository.save(strategy);
    }
}