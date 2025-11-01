package com.tradewise.api.repository;

import com.tradewise.api.model.StrategyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface StrategyRuleRepository extends JpaRepository<StrategyRule, UUID> {
}