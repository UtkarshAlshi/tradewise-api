package com.tradewise.api.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class AlphaVantageDailyResponse {
    @JsonProperty("Time Series (Daily)")
    private Map<String, AlphaVantageDailyQuote> timeSeries;
}