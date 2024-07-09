package com.orders.cabinet.service;

import com.orders.cabinet.event.OrderReceivedEvent;
import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.api.OrderPreps;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.model.db.order.PrepsInOrderDb;
import com.orders.cabinet.model.db.order.State;
import com.orders.cabinet.repository.OrderRepository;
import com.orders.cabinet.repository.PrepsInOrderRepository;
import com.orders.cabinet.repository.ShopRepository;
import com.orders.cabinet.repository.StateRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderWriterService {

    OrderRepository orderRepository;
    StateRepository stateRepository;
    PrepsInOrderRepository prepsInOrderRepository;
    ShopRepository shopRepository;
    DrugNameService drugNameService;

    @EventListener
    public void handleOrderReceivedEvent(OrderReceivedEvent event) {

        saveOrders(event.getOrder());
    }

    @Async
    @Transactional
    public void saveOrders(Order[] orders) {
        for (Order order : orders) {
            Shops shop = getShops(order);
            OrderDb orderDb = getOrderDb(order, shop);
            State state = getState(order, orderDb, shop);
            saveRpersInOrder(order, state);
        }
    }

    private Shops getShops(Order order) {
        Shops shop = shopRepository.getShopByShopId(order.getIdShop())
                .orElseThrow(() -> new NoSuchElementException("Shop not found: " + order.getIdShop()));
        return shop;
    }

    private void saveRpersInOrder(Order order, State state) {
        List<PrepsInOrderDb> prepsInOrderDbList = new ArrayList<>();
        for (OrderPreps prep : order.getData()) {
            String drugName = null;
            try {
                drugName = drugNameService.getDrugName(prep.getId()).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }
            PrepsInOrderDb prepsInOrderDb = PrepsInOrderDb.builder()
                    .state(state)
                    .morionId(prep.getId())
                    .extId(prep.getExtId())
                    .price(prep.getPrice())
                    .quant(prep.getQuant())
                    .drugName(drugName)
                    .build();
            prepsInOrderDbList.add(prepsInOrderDb);
        }
        prepsInOrderRepository.saveAll(prepsInOrderDbList);
    }

    private State getState(Order order, OrderDb orderDb, Shops shop) {
        State state = State.builder()
                .order(orderDb)
                .shop(shop)
                .time(new Date(order.getTimestamp() * 1000))
                .state(order.getState())
                .reason(order.getReason())
                .build();
        stateRepository.save(state);
        return state;
    }

    private OrderDb getOrderDb(Order order, Shops shop) {
        OrderDb orderDb = OrderDb.builder()
                .orderId(order.getIdOrder())
                .shop(shop)
                .shopExtId(order.getExtidShop())
                .phone(order.getPhone())
                .agent(order.getAgent())
                .timestamp(order.getTimestamp())
                .shipping(order.getShipping())
                .build();
        Optional<OrderDb> byShopIdAndOrderId = orderRepository.findByShopIdAndOrderId(shop.getShopId(), order.getIdOrder());
        if (byShopIdAndOrderId.isEmpty()) orderRepository.save(orderDb);
        else {
            orderDb = byShopIdAndOrderId.get();
            Hibernate.initialize(orderDb.getStates());  // Initialize the states collection
        }
        return orderDb;
    }
}
