package com.tradewise.api.dto.finnhub;

import lombok.Data;
import java.util.List;

@Data
public class FinnhubMessage {
    private String type; // e.g., "trade" or "ping"
    private List<FinnhubTrade> data;
}