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
/**
 * Custom {@link HealthIndicator} implementation for checking the health of the Telegram service.
 *
 * <p>This component implements the {@link HealthIndicator} interface to provide custom
 * health checks for the Telegram service. It uses {@link RestTemplate} to perform an HTTP GET
 * request to a ping endpoint and determines if the Telegram service is operational based on the
 * response.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramHealthIndicator implements HealthIndicator {
    /**
     * {@link RestTemplate} instance used for making HTTP requests to the Telegram service.
     */
    RestTemplate restTemplate;

    /**
     * {@link TelegramProperties} instance containing configuration properties for the Telegram service.
     */
    TelegramProperties prop;

    /**
     * Performs the health check of the Telegram service.
     *
     * <p>This method sends a ping request to the Telegram service and checks the response. If
     * the response contains the expected "pong" message, the service is considered healthy.
     * Otherwise, or if an exception occurs during the request, the service is reported as
     * unhealthy.</p>
     *
     * @return a {@link Health} object representing the current health status of the Telegram service
     */
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

    /**
     * Asynchronously performs a ping request to the Telegram service.
     *
     * <p>This method sends an HTTP GET request to the Telegram service's ping endpoint and
     * returns a {@link CompletableFuture} containing the response body.</p>
     *
     * @return a {@link CompletableFuture} containing the response body of the ping request
     */
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

    /**
     * Constructs HTTP headers for the request.
     *
     * <p>This method creates and returns {@link HttpHeaders} with the content type set to
     * {@link MediaType#APPLICATION_JSON}.</p>
     *
     * @return the {@link HttpHeaders} for the request
     */
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
