package com.tradewise.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradewise.api.dto.finnhub.FinnhubMessage;
import com.tradewise.api.dto.finnhub.FinnhubTrade;
import com.tradewise.api.dto.response.StockPriceUpdate;
import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class MarketDataBroker {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataBroker.class);

    @Value("${tradewise.app.finnhub.apikey}")
    private String finnhubApiKey;

    @Value("${tradewise.app.finnhub.websocket.url}")
    private String finnhubWsUrl;

    // A hardcoded list of symbols to subscribe to.
    // In a real app, this would be dynamic based on user portfolios.
    private static final List<String> SYMBOLS_TO_SUBSCRIBE = Arrays.asList(
            "AAPL", "MSFT", "GOOGL", "AMZN", "IBM", "BINANCE:BTCUSDT", "BINANCE:ETHUSDT"
    );

    // This is Spring's tool for sending messages to our clients
    private final SimpMessagingTemplate messagingTemplate;
    // Jackson's tool for parsing JSON
    private final ObjectMapper objectMapper;
    private WebSocketClient webSocketClient;

    public MarketDataBroker(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * This method is called automatically when the Spring application starts.
     */
    @PostConstruct
    public void connect() {
        try {
            URI uri = new URI(finnhubWsUrl + "?token=" + finnhubApiKey);

            this.webSocketClient = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    logger.info("Connected to Finnhub WebSocket.");
                    // Once connected, subscribe to our symbols
                    for (String symbol : SYMBOLS_TO_SUBSCRIBE) {
                        String subscribeMessage = String.format("{\"type\":\"subscribe\",\"symbol\":\"%s\"}", symbol);
                        logger.info("Subscribing to " + symbol);
                        this.send(subscribeMessage);
                    }
                }

                @Override
                public void onMessage(String message) {
                    try {
                        FinnhubMessage finnhubMessage = objectMapper.readValue(message, FinnhubMessage.class);

                        // We only care about "trade" messages
                        if ("trade".equalsIgnoreCase(finnhubMessage.getType()) && finnhubMessage.getData() != null) {
                            for (FinnhubTrade trade : finnhubMessage.getData()) {
                                if (trade.getSymbol() != null && trade.getPrice() != null) {
                                    broadcastPrice(trade.getSymbol(), trade.getPrice());
                                }
                            }
                        }
                    } catch (JsonProcessingException e) {
                        // Not a trade message, maybe a "ping". We can ignore it.
                        // logger.debug("Received non-trade message: " + message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    logger.warn("Disconnected from Finnhub WebSocket: " + reason);
                    // TODO: Implement reconnection logic
                }

                @Override
                public void onError(Exception ex) {
                    logger.error("Finnhub WebSocket error", ex);
                }
            };

            logger.info("Connecting to Finnhub...");
            this.webSocketClient.connectBlocking(); // Connect and wait

        } catch (Exception e) {
            logger.error("Failed to connect to WebSocket", e);
        }
    }

    /**
     * Broadcasts the price update to all connected frontend clients.
     */
    private void broadcastPrice(String symbol, BigDecimal price) {
        StockPriceUpdate update = new StockPriceUpdate(symbol, price);

        // Send to a dynamic topic based on the symbol
        String topic = "/topic/prices/" + symbol;

        logger.debug("Broadcasting update: " + topic + " - Price: " + price);
        messagingTemplate.convertAndSend(topic, update);
    }
}