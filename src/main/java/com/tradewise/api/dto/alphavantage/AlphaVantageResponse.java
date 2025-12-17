package com.tradewise.api.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// @Data // Removing Lombok's @Data
public class AlphaVantageResponse {

    @JsonProperty("Global Quote")
    private GlobalQuote globalQuote;

    // Manually added getter
    public GlobalQuote getGlobalQuote() {
        return globalQuote;
    }

    // Manually added setter
    public void setGlobalQuote(GlobalQuote globalQuote) {
        this.globalQuote = globalQuote;
    }
}
