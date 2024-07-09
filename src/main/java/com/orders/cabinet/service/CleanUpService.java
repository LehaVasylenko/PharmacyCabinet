package com.orders.cabinet.service;

import com.orders.cabinet.configuration.StatesProperties;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableScheduling
public class CleanUpService {
    OrderRepository orderRepository;
    StatesProperties statesProperties;

    //@Scheduled(cron = "0 */3 * * * *")// Testing feature each 3 minutes
    @Scheduled(cron = "0 1 0 * * *") // Runs every day at 00:01
    @Transactional
    public void cleanupOrders() {

        List<OrderDb> ordersToDelete = orderRepository
                .findAllOrders()
                .stream()
                .filter(order -> order
                        .getStates()
                        .stream()
                        .anyMatch(state -> statesProperties.getComlete().equals(state.getState()) || statesProperties.getCancel().equals(state.getState())))
                .toList();


        orderRepository.deleteAll(ordersToDelete);

        log.info("Deleted orders:\n{}", ordersToDelete.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n")));
    }
}
