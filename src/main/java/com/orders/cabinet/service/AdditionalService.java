package com.orders.cabinet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.cabinet.exception.ForbiddenAccessException;
import com.orders.cabinet.exception.ForbiddenException;
import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.mapper.OrderMapper;
import com.redis_loader.loader.model.PriceList;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.OrderRepository;
import com.orders.cabinet.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    final OrderMapper orderMapper;
    final ShopRepository shopRepository;
    final DataReaderService readerService;
    final ObjectMapper objectMapper;

    @Value("${geoapteka.api.url}")
    String geoaptUrl;


    /**
     * Retrieves all orders for a given shop.
     *
     * @param addressId the ID of the shop
     * @return a CompletableFuture containing a list of OrderDTOs for the shop
     */
    @Async
    public CompletableFuture<List<OrderDTO>> getAllOrdersForShop(String addressId) {
        checkAccess(addressId);
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        return CompletableFuture.completedFuture(ordersByShop.stream().map(orderMapper::DBToDTO).toList());
    }

    private void checkAccess(String addressId) {
        Optional<Shops> shopByShopId = shopRepository.getShopByShopId(addressId);
        if (shopByShopId.isEmpty()) throw new ForbiddenAccessException("Who are you??");
        else if (!shopByShopId.get().isLogged()) throw new ForbiddenException("Don't you forget to logIn?");
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
        checkAccess(addressId);
        //get all orders
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        //get only orders which ends by last 4 symbols
        List<OrderDb> possibleOrder = ordersByShop
                .stream()
                .filter(order -> order.getOrderId().endsWith(last))
                .toList();
        if (!possibleOrder.isEmpty()) return CompletableFuture.completedFuture(possibleOrder.stream().map(orderMapper::DBToDTO).toList());
        else return CompletableFuture.failedFuture(new NoSuchShopException("No orders, which ends with '" + last + "' for shop '" + addressId + "'!"));
    }

    @Async
    public CompletableFuture<List<PriceList>> getDrugByName(String addressId, String name) {
        checkAccess(addressId);
        if (name.trim().isEmpty()) throw new IllegalArgumentException("I need more symbols to find!");
        List<PriceList> itemsInShop = readerService.readData(addressId);
        String[] input = name.trim().split("\\s+");

        return switch (input.length) {
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
}
