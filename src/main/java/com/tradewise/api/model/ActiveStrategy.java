package com.tradewise.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "active_strategies")
@Getter
@Setter
public class ActiveStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "strategy_id", nullable = false)
    private UUID strategyId;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Manually added getter for userEmail
    public String getUserEmail() {
        return userEmail;
    }
}
