package com.tradewise.api.dto.finnhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FinnhubTrade {
    @JsonProperty("s")
    private String symbol;

    @JsonProperty("p")
    private BigDecimal price;
}