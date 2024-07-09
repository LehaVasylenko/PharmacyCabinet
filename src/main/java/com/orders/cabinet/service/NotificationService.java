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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    RestTemplate restTemplate;
    TelegramProperties properties;

    @EventListener
    public void handleOrderReceivedEvent(OrderReceivedEvent event) {

        Order[] orders = event.getOrder();
        sendNotification(orders);
    }

    @Async
    public CompletableFuture<?> sendNotification(Order[] order) {
        String url = properties.getUrl() + properties.getPath();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Order[]> requestEntity = new HttpEntity<>(order, headers);

        try {
            // Using exchange method
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("Response: {}", response);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
