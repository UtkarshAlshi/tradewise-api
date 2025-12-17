package com.tradewise.api.event;

import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

public class PriceUpdateEvent extends ApplicationEvent {
    private String symbol;
    private BigDecimal price;

    public PriceUpdateEvent(Object source, String symbol, BigDecimal price) {
        super(source);
        this.symbol = symbol;
        this.price = price;
    }

    // Manually added getters
    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
