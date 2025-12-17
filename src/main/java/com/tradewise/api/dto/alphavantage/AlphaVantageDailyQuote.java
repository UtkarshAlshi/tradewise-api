package com.tradewise.api.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// @Data // Removing Lombok's @Data
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

    // Manually added getters and setters
    public String getOpen() { return open; }
    public void setOpen(String open) { this.open = open; }

    public String getHigh() { return high; }
    public void setHigh(String high) { this.high = high; }

    public String getLow() { return low; }
    public void setLow(String low) { this.low = low; }

    public String getClose() { return close; }
    public void setClose(String close) { this.close = close; }

    public String getAdjustedClose() { return adjustedClose; }
    public void setAdjustedClose(String adjustedClose) { this.adjustedClose = adjustedClose; }

    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
}
