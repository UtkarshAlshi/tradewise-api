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

            // --- (Simulation) Let's just trigger randomly 1% of the time ---
            if (Math.random() < 0.01) {
                logger.info("SIMULATED TRIGGER: Strategy {} for symbol {} for user {}",
                        monitor.getStrategy().getName(), symbol, monitor.getUser().getEmail());

                // 4. Create the notification message
                String message = String.format(
                        "Strategy Triggered: '%s' for %s at price $%.2f",
                        monitor.getStrategy().getName(),
                        symbol,
                        event.getPrice()
                );

                // 5. Save notification to the database
                Notification notification = new Notification();
                notification.setUser(monitor.getUser());
                notification.setMessage(message);
                notification.setRead(false);
                Notification savedNotification = notificationRepository.save(notification);

                // 6. Send the notification to the *specific user*
                //    The topic is "/user/queue/notifications". Spring maps this.
                messagingTemplate.convertAndSendToUser(
                        monitor.getUser().getEmail(), // Spring finds the user's session
                        "/queue/notifications",    // The private destination
                        savedNotification              // The payload
                );
            }
        }
    }
}