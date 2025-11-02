package com.tradewise.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // This enables WebSocket message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Defines topics clients subscribe to
        config.enableSimpleBroker("/topic", "/user"); // <-- ADD "/user"

        // Defines prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/app");

        // Defines prefix for user-specific messages
        config.setUserDestinationPrefix("/user"); // <-- ADD THIS
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. Define the WebSocket endpoint
        // 2. setAllowedOrigins allows our frontend to connect
        // 3. withSockJS provides a fallback for browsers that don't support WebSockets
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }
}