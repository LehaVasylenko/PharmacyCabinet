package com.orders.cabinet.service;

import com.orders.cabinet.event.Timed;
import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.mapper.OrderMapper;
import com.orders.cabinet.model.api.PriceList;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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
    final CacheService cacheService;

    /**
     * Retrieves all orders for a given shop.
     *
     * @param addressId the ID of the shop
     * @return a CompletableFuture containing a list of OrderDTOs for the shop
     */
    @Async
    public CompletableFuture<List<OrderDTO>> getAllOrdersForShop(String addressId) {
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        return CompletableFuture.completedFuture(ordersByShop.stream().map(orderMapper::DBToDTO).toList());
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
        if (!possibleOrder.isEmpty()) return CompletableFuture.completedFuture(possibleOrder.stream().map(orderMapper::DBToDTO).toList());
        else return CompletableFuture.failedFuture(new NoSuchShopException("No orders, which ends with '" + last + "' for shop '" + addressId + "'!"));
    }

    /**
     * Retrieves drugs propositions for a given shop that starts with given symbols. In case if there are more than
     * one word, second will be found by containing.
     *
     *
     * @param addressId the ID of the shop
     * @param name the symbols of the prep to match
     * @return a CompletableFuture containing a list of matching OrderDTOs
     * @throws NoSuchShopException if no matching orders are found
     */
    @Timed
    @Async
    public CompletableFuture<List<PriceList>> getDrugByName(String addressId, String name) {
        if (name.trim().isEmpty()) throw new IllegalArgumentException("I need more symbols to find!");

        List<PriceList> itemsInShop = cacheService.getCachedPriceList(addressId);
        String[] input = name.trim().toLowerCase().split("\\s+");

        List<PriceList> filteredItems = itemsInShop.stream()
                .filter(item -> {
                    String drugNameLower = item.getDrugName().toLowerCase();
                    return switch (input.length) {
                        case 2 -> drugNameLower.startsWith(input[0]) && drugNameLower.contains(input[1]);
                        case 3 ->
                                drugNameLower.startsWith(input[0]) && drugNameLower.contains(input[1]) && drugNameLower.contains(input[2]);
                        default -> drugNameLower.startsWith(input[0]);
                    };
                })
                .sorted(Comparator.comparing(PriceList::getDrugName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        return CompletableFuture.completedFuture(filteredItems);
    }
}
