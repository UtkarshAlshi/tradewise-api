package com.tradewise.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

@Getter
public class PriceUpdateEvent extends ApplicationEvent {

    private final String symbol;
    private final BigDecimal price;

    public PriceUpdateEvent(Object source, String symbol, BigDecimal price) {
        super(source);
        this.symbol = symbol;
        this.price = price;
    }
}