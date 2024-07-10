package com.orders.cabinet.service;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.mapper.OrderMapper;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdditionalService {

    OrderRepository orderRepository;
    OrderMapper mapper;

    @Async
    public CompletableFuture<?> getAllOrdersForShop(String addressId) {
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        return CompletableFuture.completedFuture(ordersByShop.stream().map(mapper::DBToDTO).toList());
    }

    @Async
    public CompletableFuture<?> getOrderBy4LastSymbols(String addressId, String last) {
        List<OrderDb> ordersByShop = orderRepository.findByShopId(addressId);
        List<OrderDb> possibleOrder = ordersByShop.stream().filter(order -> order.getOrderId().endsWith(last)).toList();
        if (!possibleOrder.isEmpty()) return CompletableFuture.completedFuture(possibleOrder.stream().map(mapper::DBToDTO));
        else return CompletableFuture.failedFuture(new NoSuchShopException("No orders, which ends with " + last + " for shop " + addressId + "!"));
    }

}
