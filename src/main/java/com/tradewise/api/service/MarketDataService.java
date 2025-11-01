package com.tradewise.api.service;

import com.tradewise.api.dto.alphavantage.AlphaVantageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class MarketDataService {

    private final RestTemplate restTemplate;

    @Value("${tradewise.app.alphavantage.baseurl}")
    private String baseUrl;

    @Value("${tradewise.app.alphavantage.apikey}")
    private String apiKey;

    public MarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the latest quote for a given stock symbol.
     */
    public Optional<BigDecimal> getLatestPrice(String symbol) {
        // 1. Build the URL
        String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                baseUrl, symbol, apiKey);

        try {
            // 2. Call the API and map the response to our DTO
            AlphaVantageResponse response = restTemplate.getForObject(url, AlphaVantageResponse.class);

            // 3. Check for valid response
            if (response != null && response.getGlobalQuote() != null) {
                return Optional.of(response.getGlobalQuote().getPrice());
            } else {
                return Optional.empty(); // No quote found
            }
        } catch (Exception e) {
            // Log the error in a real app
            System.err.println("Error fetching market data for " + symbol + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}