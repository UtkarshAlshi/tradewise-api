package com.tradewise.api.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

// @Data // Removing Lombok's @Data
public class FinnhubTrade {
    @JsonProperty("s")
    private String symbol;

    @JsonProperty("p")
    private BigDecimal price;

    // Manually added getters and setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
