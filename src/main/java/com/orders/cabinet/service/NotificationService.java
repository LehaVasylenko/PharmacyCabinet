package com.orders.cabinet.service;

import com.orders.cabinet.configuration.TelegramProperties;
import com.orders.cabinet.event.OrderReceivedEvent;
import com.orders.cabinet.model.api.Order;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
/**
 * Service send notifications about orders to Telegram App.
 *
 * <p>This service listens for order received events and sends notifications to a specified endpoint.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    RestTemplate restTemplate;
    TelegramProperties properties;

    /**
     * Handles the event when an order is received and triggers the notification process.
     *
     * <p>This method listens for {@link OrderReceivedEvent} and extracts the order details from the event.
     * It then initiates the process of sending notifications.</p>
     *
     * @param event The {@link OrderReceivedEvent} containing the order details.
     */
    @EventListener
    public void handleOrderReceivedEvent(OrderReceivedEvent event) {

        Order[] orders = event.getOrder();
        sendNotification(orders);
    }

    /**
     * Sends a notification containing the provided orders to the specified URL.
     *
     * <p>This method is asynchronous and sends a POST request to the configured URL with the order details.
     * It handles any exceptions that may occur during the process and logs the response.</p>
     *
     * @param order An array of {@link Order} objects to be sent in the notification.
     */
    @Async
    public void sendNotification(Order[] order) {
        String url = properties.getUrl() + properties.getPath();
        for (int i = 0; i < order.length; i++) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Order> requestEntity = new HttpEntity<>(order[i], headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                log.info("Response: {}", response);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
