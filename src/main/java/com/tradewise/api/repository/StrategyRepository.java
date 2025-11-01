package com.tradewise.api.repository;

import com.tradewise.api.model.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, UUID> {
    List<Strategy> findByUserId(UUID userId);
}