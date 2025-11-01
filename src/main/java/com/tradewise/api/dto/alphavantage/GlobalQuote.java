package com.tradewise.api.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GlobalQuote {

    @JsonProperty("01. symbol")
    private String symbol;

    @JsonProperty("05. price")
    private BigDecimal price;

    @JsonProperty("07. latest trading day")
    private String latestTradingDay;
}