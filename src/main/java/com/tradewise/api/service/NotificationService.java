package com.tradewise.api.service;

import com.tradewise.api.event.PriceUpdateEvent;
import com.tradewise.api.model.ActiveStrategy;
import com.tradewise.api.model.Notification;
import com.tradewise.api.repository.ActiveStrategyRepository;
import com.tradewise.api.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final ActiveStrategyRepository activeStrategyRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(ActiveStrategyRepository activeStrategyRepository,
                               NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.activeStrategyRepository = activeStrategyRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * This method automatically listens for PriceUpdateEvents.
     */
    @EventListener
    @Transactional
    public void handlePriceUpdate(PriceUpdateEvent event) {
        String symbol = event.getSymbol();

        // 1. Find all "active monitors" for this symbol
        List<ActiveStrategy> monitors = activeStrategyRepository.findAllBySymbolAndIsActive(symbol, true);

        if (monitors.isEmpty()) {
            return; // No one is watching this symbol.
        }

        // ---
        // 2. TODO: Implement Real Strategy Check
        //    For now, we will just *simulate* a trigger.
        //    In a real system, we would:
        //    - Fetch recent bar data for the symbol.
        //    - Get the unique strategy IDs from the 'monitors'.
        //    - For each unique strategy, run its rules (using ta4j) against the data.
        //    - If a rule is met, then proceed.
        // ---

        // 3. (Simulation) Assume the strategy triggered. Create and send notifications.
        for (ActiveStrategy monitor : monitors) {
            // Always trigger for now
            // logger.info("TEST TRIGGER: Strategy {} for symbol {} for user {}",
            //         monitor.getStrategy().getName(), symbol, monitor.getUser().getEmail()); // Removed direct access

            String message = String.format(
                    "TEST notification — Strategy '%s' triggered for %s at price $%.2f",
                    "Simulated Strategy", // Placeholder for strategy name
                    symbol,
                    event.getPrice()
            );

            Notification notification = new Notification();
            // notification.setUser(monitor.getUser()); // Removed direct User object
            notification.setUserEmail(monitor.getUserEmail()); // Use user email from ActiveStrategy
            notification.setMessage(message);
            notification.setRead(false);
            Notification savedNotification = notificationRepository.save(notification);

            // For now, broadcast globally
            messagingTemplate.convertAndSend("/topic/notifications", savedNotification);
        }
    }
}
