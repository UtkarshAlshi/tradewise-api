package com.tradewise.api.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlphaVantageDailyQuote {
    @JsonProperty("1. open")
    private String open;
    @JsonProperty("2. high")
    private String high;
    @JsonProperty("3. low")
    private String low;
    @JsonProperty("4. close")
    private String close;
    @JsonProperty("5. adjusted close")
    private String adjustedClose;
    @JsonProperty("6. volume")
    private String volume;
}