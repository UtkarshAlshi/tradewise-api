package com.tradewise.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode; // <-- ADD THIS
import org.hibernate.type.SqlTypes; // <-- ADD THIS

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "strategy_conditions")
@Getter
@Setter
public class StrategyCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private StrategyRule rule;

    @Column(name = "indicator_a", nullable = false)
    private String indicatorA; // "RSI", "SMA", "PRICE"

    @JdbcTypeCode(SqlTypes.JSON) // <-- This tells Hibernate to treat this as JSON
    @Column(name = "indicator_a_params", columnDefinition = "jsonb")
    private Map<String, Object> indicatorAParams; // {"period": 14}

    @Column(nullable = false)
    private String operator; // "GREATER_THAN", "CROSSES_ABOVE"

    @Column(name = "indicator_b_type", nullable = false)
    private String indicatorBType; // "VALUE" or "INDICATOR"

    @Column(name = "indicator_b_value", nullable = false)
    private String indicatorBValue; // "70" or "SMA"

    @JdbcTypeCode(SqlTypes.JSON) // <-- This tells Hibernate to treat this as JSON
    @Column(name = "indicator_b_params", columnDefinition = "jsonb")
    private Map<String, Object> indicatorBParams; // {"period": 50}
}