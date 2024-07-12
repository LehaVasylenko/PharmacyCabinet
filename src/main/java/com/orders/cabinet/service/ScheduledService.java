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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


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

    private List<ShopsDTO> getAvailableShops() {
        List<ShopsDTO> result = repo.findAllByLoggedTrue().stream().map(mapper::toDto).collect(Collectors.toList());
        log.info(result.toString());
        return result;
    }


    //@Scheduled(fixedRateString = "${pop-order.rate}")
    @Scheduled(fixedRate = 10000)
    public void makeRequest() {

        List<ShopsDTO> result = getAvailableShops();
        if (!result.isEmpty()) {
            for (ShopsDTO shop : result) {
                doFuckingMagicAsync(shop);
            }
        }
    }

    @Async
    public CompletableFuture<Void> doFuckingMagicAsync(ShopsDTO shop) {
        try {
            doFuckingMagic(shop);
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("Error processing shop {}: {}", shop.getShopId(), e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

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

    private HttpHeaders getHttpHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", getAuthHeader(username, password));
        headers.set("User-Agent", properties.getAgent());
        return headers;
    }

    private String getAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }


}
