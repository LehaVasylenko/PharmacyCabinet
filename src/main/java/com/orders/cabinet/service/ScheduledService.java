package com.orders.cabinet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.cabinet.configuration.PopOrderProperties;
import com.orders.cabinet.event.OrderReceivedEvent;
import com.orders.cabinet.mapper.ShopMapper;
import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.db.dto.CorpDTO;
import com.orders.cabinet.model.db.dto.ShopsDTO;
import com.orders.cabinet.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for periodically retrieving orders from available shops and processing them.
 *
 * <p>This service is scheduled to periodically make requests to retrieve orders from shops
 * that are currently logged in. It processes these orders and publishes events for them.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableScheduling
public class ScheduledService {

    final RestTemplate restTemplate;
    final ObjectMapper objectMapper;
    final PopOrderProperties properties;
    final AdminService service;
    final ApplicationEventPublisher eventPublisher;
    final ShopRepository repo;
    final ShopMapper mapper;

    Map<String, CorpDTO> corps = new HashMap<>();

    /**
     * Retrieves a list of available shops that are currently logged in.
     *
     * @return a list of {@link ShopsDTO} representing available shops.
     */
    private List<ShopsDTO> getAvailableShops() {
        List<ShopsDTO> result = repo.findAllByLoggedTrue().stream().map(mapper::toDto).collect(Collectors.toList());
        log.info(result.toString());
        return result;
    }

    /**
     * Periodically retrieves orders from available shops and processes them.
     *
     * <p>This method is scheduled to run at a fixed rate (every 10 seconds) and
     * processes orders for each available shop asynchronously.</p>
     */
    @Scheduled(cron = "${pop-order.rate}")
    //@Scheduled(fixedRate = 10000)
    public void makeRequest() {

        List<ShopsDTO> result = getAvailableShops();
        if (!result.isEmpty()) {
            for (ShopsDTO shop : result) {
                doFuckingMagicAsync(shop);
            }
        }
    }

    /**
     * Asynchronously processes orders for a specific shop.
     *
     * @param shop the {@link ShopsDTO} representing the shop to process.
     * @return a {@link CompletableFuture} indicating the completion of the task.
     */
    @Async
    public CompletableFuture<Void> doFuckingMagicAsync(ShopsDTO shop) {
        try {
            doFuckingMagic(shop);
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("Error processing shop {}: {}", shop.getShopId(), e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Processes orders for a specific shop by making an HTTP request to retrieve them.
     *
     * @param shop the {@link ShopsDTO} representing the shop to process.
     * @throws InterruptedException if the thread is interrupted while waiting.
     * @throws ExecutionException if an exception occurs during asynchronous processing.
     * @throws JsonProcessingException if there is an error processing JSON.
     */
    private void doFuckingMagic(ShopsDTO shop) throws InterruptedException, ExecutionException, JsonProcessingException {
        String url = properties.getUrl() + properties.getPop();
        CorpDTO temp = null;
        if (corps.containsKey(shop.getCorpId())) {
            temp = corps.get(shop.getCorpId());
        } else {
            temp = service.getCorpInfoById(shop.getCorpId()).get();
            corps.put(shop.getCorpId(), temp);
        }

        List<String> requestBody = List.of(shop.getShopId());

        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, getHttpHeaders(temp.getLogin(), temp.getPassword()));

            log.info(entity.toString());
            ResponseEntity<Order[]> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Order[].class);
            log.info(exchange.toString());
            if (exchange.getBody() != null) {
                for (Order order : exchange.getBody()) {
                    log.info(order.toString());
                }
            }

            Order[] response = exchange.getBody();
            if (response != null) {
                eventPublisher.publishEvent(new OrderReceivedEvent(this, response));
            }
        } catch (HttpClientErrorException ex) {
            log.error(LocalDateTime.now() + ": " + ex.getStatusCode() + ": " + ex.getMessage());
        }
    }

    /**
     * Creates HTTP headers for the request including authorization and content type.
     *
     * @param username the username for authorization.
     * @param password the password for authorization.
     * @return the {@link HttpHeaders} to be used in the HTTP request.
     */
    private HttpHeaders getHttpHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", getAuthHeader(username, password));
        headers.set("User-Agent", properties.getAgent());
        return headers;
    }

    /**
     * Creates an authorization header for basic authentication.
     *
     * @param username the username for authorization.
     * @param password the password for authorization.
     * @return the authorization header as a {@link String}.
     */
    private String getAuthHeader(String username, String password) {
        String auth = new StringBuilder().append(username).append(":").append(password).toString();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }


}
