package com.orders.cabinet.health;

import com.orders.cabinet.configuration.TelegramProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramHealthIndicator implements HealthIndicator {
    RestTemplate restTemplate;
    TelegramProperties prop;
    @Override
    public Health health() {
        try {
            String stringCompletableFuture = checkPingPong().get();
            if (stringCompletableFuture.equals("pong"))
                return Health.up().withDetail("message", "Telegram is healthy").build();
        } catch (InterruptedException | ExecutionException e) {
            return Health.down().withDetail("message", "Telegram is ill").build();
        }
        return Health.down().withDetail("message", "Telegram is ill").build();
    }

    @Async
    public CompletableFuture<String> checkPingPong() {
        String url = prop.getUrl() + prop.getPing();

        HttpEntity<?> request = new HttpEntity<>(getHeaders());

        ResponseEntity<String> exchange = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        return CompletableFuture.completedFuture(exchange.getBody());
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
