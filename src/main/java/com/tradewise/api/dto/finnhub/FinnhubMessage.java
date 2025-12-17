package com.tradewise.api.dto.finnhub;

import lombok.Data;
import java.util.List;

// @Data // Removing Lombok's @Data
public class FinnhubMessage {
    private String type;
    private List<FinnhubTrade> data;

    // Manually added getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FinnhubTrade> getData() {
        return data;
    }

    public void setData(List<FinnhubTrade> data) {
        this.data = data;
    }
}
