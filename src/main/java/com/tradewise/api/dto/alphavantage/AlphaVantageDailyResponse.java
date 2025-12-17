package com.tradewise.api.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

// @Data // Removing Lombok's @Data
public class AlphaVantageDailyResponse {
    @JsonProperty("Time Series (Daily)")
    private Map<String, AlphaVantageDailyQuote> timeSeries;

    // Manually added getter
    public Map<String, AlphaVantageDailyQuote> getTimeSeries() {
        return timeSeries;
    }

    // Manually added setter
    public void setTimeSeries(Map<String, AlphaVantageDailyQuote> timeSeries) {
        this.timeSeries = timeSeries;
    }
}
