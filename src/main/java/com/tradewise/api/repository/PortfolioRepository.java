package com.tradewise.api.repository;

import com.tradewise.api.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

    // Spring Data JPA will automatically create a method to find all
    // portfolios owned by a specific user. We'll use this later.
    // List<Portfolio> findByUserId(UUID userId);

    /**
     * Finds all portfolios owned by a specific user.
     * Spring Data JPA creates the query for us based on the method name.
     */
    List<Portfolio> findByUserId(UUID userId);
}