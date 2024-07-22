package com.orders.cabinet.service;

import com.orders.cabinet.event.OrderReceivedEvent;
import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.api.OrderPreps;
import com.orders.cabinet.model.db.DrugCache;
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
/**
 * Service for handling and saving orders to the database.
 *
 * <p>This service listens for order received events, processes the orders, and saves the relevant
 * information into the database.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
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

    /**
     * Handles the event when orders are received and initiates the process of saving them.
     *
     * <p>This method listens for {@link OrderReceivedEvent} and extracts the order details from the event.
     * It then processes and saves the orders.</p>
     *
     * @param event The {@link OrderReceivedEvent} containing the order details.
     */
    @EventListener
    public void handleOrderReceivedEvent(OrderReceivedEvent event) {

        saveOrders(event.getOrder());
    }

    /**
     * Processes and saves the provided orders.
     *
     * <p>This asynchronous method iterates over the provided orders, retrieves the corresponding shop,
     * creates and saves the order, and updates the state of the order. It also saves the preparations
     * associated with the order.</p>
     *
     * @param orders An array of {@link Order} objects to be processed and saved.
     */
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

    /**
     * Retrieves the {@link Shops} entity for the given order.
     *
     * <p>Fetches the shop from the repository using the shop ID from the order. Throws an exception if
     * the shop is not found.</p>
     *
     * @param order The {@link Order} containing the shop ID.
     * @return The {@link Shops} entity corresponding to the shop ID.
     * @throws NoSuchElementException If no shop is found with the given ID.
     */
    private Shops getShops(Order order) {
        Shops shop = shopRepository.getShopByShopId(order.getIdShop())
                .orElseThrow(() -> new NoSuchElementException("Shop not found: " + order.getIdShop()));
        return shop;
    }

    /**
     * Saves the preparations associated with the order to the database.
     *
     * <p>This method retrieves the drug name asynchronously and constructs {@link PrepsInOrderDb} objects
     * for each preparation in the order. It then saves all the preparations to the repository.</p>
     *
     * @param order The {@link Order} containing the preparations.
     * @param state The {@link State} of the order.
     */
    private void saveRpersInOrder(Order order, State state) {
        List<PrepsInOrderDb> prepsInOrderDbList = new ArrayList<>();
        for (OrderPreps prep : order.getData()) {
            DrugCache drugName = null;
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
                    .drugName(drugName.getDrugName())
                    .drugLink(drugName.getDrugLink())
                    .build();
            prepsInOrderDbList.add(prepsInOrderDb);
        }
        prepsInOrderRepository.saveAll(prepsInOrderDbList);
    }

    /**
     * Creates and saves the {@link State} for the given order.
     *
     * <p>Constructs a {@link State} object for the order and saves it to the repository.</p>
     *
     * @param order The {@link Order} for which the state is being created.
     * @param orderDb The {@link OrderDb} entity associated with the order.
     * @param shop The {@link Shops} entity associated with the order.
     * @return The created {@link State} object.
     */
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

    /**
     * Retrieves the {@link OrderDb} entity for the given order and shop.
     *
     * <p>Creates a new {@link OrderDb} object if it does not exist in the repository. Initializes
     * the states collection if the order already exists.</p>
     *
     * @param order The {@link Order} containing the order details.
     * @param shop The {@link Shops} entity associated with the order.
     * @return The {@link OrderDb} entity corresponding to the order.
     */
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
