package com.tradewise.api.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // ✅ Simple health check endpoint
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // ✅ Broadcast to everyone subscribed to /topic/notifications
    @PostMapping("/notify")
    public String sendNotification(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "Hello from backend!");
        messagingTemplate.convertAndSend("/topic/notifications",
                Map.of("message", message));
        return "Notification sent!";
    }

    // ✅ Send a private message to a specific user (via /user/{username}/queue/notifications)
    @PostMapping("/notify/{username}")
    public String sendPrivateNotification(@PathVariable String username, @RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "Private hello!");
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications",
                Map.of("message", message));
        return "Private notification sent to " + username;
    }
}
