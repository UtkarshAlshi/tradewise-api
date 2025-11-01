package com.tradewise.api.repository;

import com.tradewise.api.model.PortfolioAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface PortfolioAssetRepository extends JpaRepository<PortfolioAsset, UUID> {
    // We'll add methods here later, e.g., findAllByPortfolioId(UUID portfolioId)
    /**
     * Finds all assets for a specific portfolio.
     * Spring Data JPA builds the query from the method name.
     */
    List<PortfolioAsset> findAllByPortfolioId(UUID portfolioId);
}