package com.orders.cabinet.service;

import com.orders.cabinet.configuration.TelegramProperties;
import com.orders.cabinet.model.api.dto.NotificationDTO;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
/**
 * Service for managing and notifying about orders based on their state.
 *
 * <p>This service periodically checks for orders that have a specific state and
 * sends notifications to Telegram App for orders which are wait too long for confirmation.</p>
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
@EnableScheduling
public class RememberAllService {
    OrderRepository repository;
    TelegramProperties properties;
    RestTemplate restTemplate;

    /**
     * Periodically checks for orders with only one state and sends notifications
     * for orders that are eligible based on their state timing.
     *
     * <p>This asynchronous and scheduled method retrieves orders that have only
     * one state and checks if the last state timestamp plus 40 minutes is after
     * the current time. If so, it sends a notification for each eligible order.</p>
     *
     * @see OrderDb
     * @see NotificationDTO
     */
    @Async
    @Scheduled(cron = "${scheduled.cron}")
    public void rememberOrder() {
        List<OrderDb> newOrders = repository.findOrdersWithOnlyOneState()
                .stream()
                .filter(order -> order.getStates()
                        .get(order.getStates().size() - 1)
                        .getTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusMinutes(40L)
                        .isAfter(LocalDateTime.now()))
                .toList();

        if (!newOrders.isEmpty()) {
            for (OrderDb order: newOrders) {
                NotificationDTO notific = NotificationDTO
                        .builder()
                        .orderId(order.getOrderId())
                        .shopId(order.getShop().getShopId())
                        .build();

                String url = properties.getUrl() + properties.getNotificator();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<NotificationDTO> requestEntity = new HttpEntity<>(notific, headers);

                try {
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                    log.info("Response: {}", response);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
