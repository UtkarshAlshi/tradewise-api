package com.tradewise.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "portfolio_assets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String symbol; // e.g., "AAPL", "BTC-USD"

    @Column(nullable = false, precision = 19, scale = 8) // Allows for large numbers and precision
    private BigDecimal quantity;

    @Column(name = "purchase_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    // An asset belongs to one portfolio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
}