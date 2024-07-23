package com.orders.cabinet.service;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.mapper.OrderMapper;
import com.orders.cabinet.model.api.PriceList;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.DrugCache;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.DrugCacheRepository;
import com.orders.cabinet.repository.OrderRepository;
import com.orders.cabinet.repository.ShopRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for handling additional order-related operations.
 *
 * <p>This service provides asynchronous methods for fetching all orders for a shop
 * and for fetching orders by the last four symbols of their ID.</p>
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
public class AdditionalService {

    final OrderRepository orderRepository;
    final OrderMapper mapper;
    final RestTemplate restTemplate;
    final DrugNameService drugNameService;
    final ShopRepository shopRepository;
    final DrugCacheRepository drugCacheRepository;

    @Value("${geoapteka.api.url}")
    String geoaptUrl;

    Map<String, List<PriceList>> priceListCache = new HashMap<>();

    /**
     * Retrieves all orders for a given shop.
     *
     * @param addressId the ID of the shop
     * @return a CompletableFuture containing a list of OrderDTOs for the shop
     */
    @Async
    public CompletableFuture<List<OrderDTO>> getAllOrdersForShop(String addressId) {
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        return CompletableFuture.completedFuture(ordersByShop.stream().map(mapper::DBToDTO).toList());
    }

    /**
     * Retrieves orders for a given shop that end with the specified last four symbols.
     *
     * @param addressId the ID of the shop
     * @param last the last four symbols of the order IDs to match
     * @return a CompletableFuture containing a list of matching OrderDTOs
     * @throws NoSuchShopException if no matching orders are found
     */
    @Async
    public CompletableFuture<List<OrderDTO>> getOrderBy4LastSymbols(String addressId, String last) {
        //get all orders
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        //get only orders which ends by last 4 symbols
        List<OrderDb> possibleOrder = ordersByShop
                .stream()
                .filter(order -> order.getOrderId().endsWith(last))
                .toList();
        if (!possibleOrder.isEmpty()) return CompletableFuture.completedFuture(possibleOrder.stream().map(mapper::DBToDTO).toList());
        else return CompletableFuture.failedFuture(new NoSuchShopException("No orders, which ends with '" + last + "' for shop '" + addressId + "'!"));
    }

    @Async
    public CompletableFuture<List<PriceList>> getDrugByName(String addressId, String name) {
        if (name.trim().isEmpty()) throw new IllegalArgumentException("I need more symbols to find!");
        List<PriceList> itemsInShop = this.priceListCache.get(addressId);
        if (itemsInShop == null) {
            try {
                setCacheForPriceListByShop(addressId);
            } catch (InterruptedException | ExecutionException e) {
                return CompletableFuture.failedFuture(e);
            }
            itemsInShop = this.priceListCache.get(addressId);
        }
        String[] input = name.trim().split("\\s+");

        return switch (input.length) {
//            case 1 -> CompletableFuture.completedFuture(itemsInShop
//                    .stream()
//                    .filter(item -> item.getDrugName().toLowerCase().startsWith(input[0].toLowerCase()))
//                    .toList());
            case 2 -> CompletableFuture.completedFuture(itemsInShop
                    .stream()
                    .filter(item -> item.getDrugName().toLowerCase().startsWith(input[0].toLowerCase())
                            && item.getDrugName().toLowerCase().contains(input[1].toLowerCase()))
                    .sorted((item1, item2) -> item1.getDrugName().compareToIgnoreCase(item2.getDrugName()))
                    .toList());
            case 3 -> CompletableFuture.completedFuture(itemsInShop
                    .stream()
                    .filter(item -> item.getDrugName().toLowerCase().startsWith(input[0].toLowerCase())
                            && item.getDrugName().toLowerCase().contains(input[1].toLowerCase())
                            && item.getDrugName().toLowerCase().contains(input[2].toLowerCase()))
                    .sorted((item1, item2) -> item1.getDrugName().compareToIgnoreCase(item2.getDrugName()))
                    .toList());
            default -> CompletableFuture.completedFuture(itemsInShop
                    .stream()
                    .filter(item -> item.getDrugName().toLowerCase().startsWith(input[0].toLowerCase()))
                    .sorted((item1, item2) -> item1.getDrugName().compareToIgnoreCase(item2.getDrugName()))
                    .toList());
        };


    }

    private void setCacheForPriceListByShop(String addressId) throws ExecutionException, InterruptedException {
        List<PriceList> propsByShop = getPropsByShop(addressId);
        List<String> drugIds = propsByShop.stream()
                .map(PriceList::getDrugId)
                .toList();

        drugNameService.setDrugNames(drugIds);

        propsByShop.forEach(priceList -> {
            Optional<DrugCache> drugCache = drugCacheRepository.findById(priceList.getDrugId());
            if (drugCache.isPresent()) {
                priceList.setDrugName(drugCache.get().getDrugName());
                priceList.setDrugLink(drugCache.get().getDrugLink());
            } else {
                priceList.setDrugName("Some drug");
                priceList.setDrugLink("Some link");
            }
        });
        this.priceListCache.put(addressId, propsByShop);
    }

    public List<PriceList> getPropsByShop(String shopId) {
        String apiUrl = geoaptUrl + "/get_props_by_shop/" + shopId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info(shopId);
        ResponseEntity<List<PriceList>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        log.info("{} -> {}",response.getStatusCode(), response.getBody().size());
        return response.getBody();
    }

    @Scheduled(fixedRate = 900000)
    public void cleanCache() {
        cachePriceList();
    }

    @PostConstruct
    public void init() {
        cachePriceList();
    }

    private void cachePriceList() {
        for (Shops shop: shopRepository.findAllByLoggedTrue()) {
            try {
                setCacheForPriceListByShop(shop.getShopId());
            } catch (ExecutionException | InterruptedException e) {
                log.error("Can't get price list for '{}' shop!", shop.getShopId());
            }
        }
    }
}
