package com.tradewise.api.repository;

import com.tradewise.api.model.StrategyCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface StrategyConditionRepository extends JpaRepository<StrategyCondition, UUID> {
}