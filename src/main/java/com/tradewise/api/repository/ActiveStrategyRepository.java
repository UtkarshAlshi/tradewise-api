package com.tradewise.api.repository;

import com.tradewise.api.model.ActiveStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActiveStrategyRepository extends JpaRepository<ActiveStrategy, UUID> {

    // Find all active monitors for a specific symbol
    List<ActiveStrategy> findAllBySymbolAndIsActive(String symbol, boolean isActive);
}