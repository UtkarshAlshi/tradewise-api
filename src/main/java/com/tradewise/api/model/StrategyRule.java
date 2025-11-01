package com.tradewise.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "strategy_rules")
@Getter
@Setter
public class StrategyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(nullable = false)
    private String action; // "BUY" or "SELL"

    @Column(name = "action_amount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal actionAmountPercent; // e.g., 100.00, 50.00

    @Column(nullable = false)
    private int priority; // e.g., 1

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<StrategyCondition> conditions;
}