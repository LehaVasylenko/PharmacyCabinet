package com.orders.cabinet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.cabinet.configuration.PopOrderProperties;
import com.orders.cabinet.configuration.StatesProperties;
import com.orders.cabinet.event.OrderReceivedEvent;
import com.orders.cabinet.exception.ImpossibleException;
import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.exception.OrderOutOfDateException;
import com.orders.cabinet.mapper.OrderMapper;
import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.api.OrderPreps;
import com.orders.cabinet.model.api.dto.ControllerDTO;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.model.db.order.PrepsInOrderDb;
import com.orders.cabinet.model.db.order.State;
import com.orders.cabinet.repository.OrderRepository;
import com.orders.cabinet.repository.PrepsInOrderRepository;
import com.orders.cabinet.repository.ShopRepository;
import com.orders.cabinet.repository.StateRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
/**
 * Service for handling and updating orders based on received events and requests.
 *
 * <p>This service listens for order events, updates orders' states, and communicates with external systems to confirm, complete, or cancel orders.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class UpdateOrderService {

    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    PopOrderProperties properties;
    OrderMapper mapper;

    StateRepository stateRepository;
    OrderRepository orderRepository;
    PrepsInOrderRepository prepsInOrderRepository;
    ShopRepository shopRepository;

    StatesProperties states;
    Map<String, List<Order>> orderMap = new HashMap<>();
    OrderWriterService orderWriterService;

    /**
     * Handles the {@link OrderReceivedEvent} by adding received orders to the internal map.
     *
     * @param event the {@link OrderReceivedEvent} containing the received orders.
     */
    @EventListener
    public void handleOrderReceivedEvent(OrderReceivedEvent event) {
        Order[] newOrders = event.getOrder();
        Arrays.stream(newOrders)
                .forEach(order -> {
                    orderMap
                            .computeIfAbsent(order.getIdShop(), key -> new ArrayList<>())
                            .add(order);
                });
    }

    /**
     * Retrieves and removes new orders for a specific shop.
     *
     * @param shopId the ID of the shop for which to retrieve orders.
     * @return a {@link CompletableFuture} containing a list of {@link OrderDTO} representing the orders.
     * @throws NoSuchShopException if no orders are available for the specified shop ID.
     */
    @Async
    public CompletableFuture<List<OrderDTO>> getOrdersWithOnlyNewStateByShopId(String shopId) {
        if (orderMap.get(shopId) != null) {
            List<Order> orders = new ArrayList<>(orderMap.get(shopId));
            orderMap.remove(shopId);

            return CompletableFuture.supplyAsync(() ->
                    orders.stream().map(mapper::OrderToDto).collect(Collectors.toList())
            );
        } else {
            return CompletableFuture.failedFuture(new NoSuchShopException(""));
        }
    }

    /**
     * Confirms an order by updating its state and processing it.
     *
     * @param shopId the ID of the shop where the order is located.
     * @param controllerDto the {@link ControllerDTO} containing order details.
     * @return a {@link CompletableFuture} containing the updated state.
     */
    @Async
    public CompletableFuture<?> confirmOrder(String shopId, ControllerDTO controllerDto) {
        try {
            Order newOrder = getNewState(shopId, controllerDto, states.getConfirm());

            doPartyHard(newOrder);
            saveIt(newOrder);
            return CompletableFuture.completedFuture(states.getConfirm());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Completes an order by updating its state and processing it.
     *
     * @param shopId the ID of the shop where the order is located.
     * @param controllerDto the {@link ControllerDTO} containing order details.
     * @return a {@link CompletableFuture} containing the updated state.
     */
    @Async
    public CompletableFuture<?> completeOrder(String shopId, ControllerDTO controllerDto) {
        try {
            Order newOrder = getNewState(shopId, controllerDto, states.getComlete());

            doPartyHard(newOrder);
            saveIt(newOrder);

            return CompletableFuture.completedFuture(states.getComlete());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Cancels an order by updating its state and processing it.
     *
     * @param shopId the ID of the shop where the order is located.
     * @param controllerDto the {@link ControllerDTO} containing order details.
     * @return a {@link CompletableFuture} containing the updated state.
     */
    @Async
    public CompletableFuture<?> cancelOrder(String shopId, ControllerDTO controllerDto) {
        try {
            Order newOrder = getNewState(shopId, controllerDto, states.getCancel());
            newOrder.setReason(controllerDto.getReason());

            doPartyHard(newOrder);
            saveIt(newOrder);

            return CompletableFuture.completedFuture(states.getCancel());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Saves the given order using the {@link OrderWriterService}.
     *
     * @param newOrder the {@link Order} to be saved.
     */
    private void saveIt(Order newOrder) {
        newOrder.setTimestamp(Instant.now().getEpochSecond());
        orderWriterService.saveOrders(new Order[]{newOrder});
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
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }

    /**
     * Sends the order to an external system for processing.
     *
     * @param newOrder the {@link Order} to be processed.
     * @throws IllegalStateException if the external system responds with an error.
     * @throws IllegalArgumentException if there is an error processing JSON.
     * @throws OrderOutOfDateException if the order is outdated and canceled by the external system.
     */
    private void doPartyHard(Order newOrder) {
        Optional<Corp> corp = shopRepository.findCorpByShopId(newOrder.getIdShop());
        boolean noContent = false;
        boolean notEnought = true;
        if (corp.isPresent()) {
            ResponseEntity<String> response = null;
            do {
                try {
                    String url = properties.getUrl() + properties.getUpd();
                    String requestBodyJson = objectMapper.writeValueAsString(newOrder);
                    HttpEntity<String> entity = new HttpEntity<>(requestBodyJson,
                            getHttpHeaders(corp.get().getLogin(), corp.get().getPassword()));
                    response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                    log.info(response + ":\n" + requestBodyJson);
                    if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                        noContent = true;
                        notEnought = false;
                    }
                    if (response.getStatusCode() == HttpStatus.OK) notEnought = false;
                } catch (HttpClientErrorException ex) {
                    log.error(ex.getStatusCode() + ": " + ex.getMessage());
                    throw new IllegalStateException(ex.getStatusCode() + " -> " + ex.getMessage());
                } catch (JsonProcessingException ex) {
                    log.error(ex.getMessage());
                    throw new IllegalArgumentException(ex.getMessage());
                }
                if (noContent) throw new OrderOutOfDateException("Order " + newOrder.getIdOrder() + " expired and canceled by Booking!");
            } while (notEnought);
        } else {
            log.error("Can't be sent: {}", newOrder);
            throw new ImpossibleException("No data for communication with booking");
        }
    }

    /**
     * Constructs a new order state based on the provided shop ID and controller DTO.
     *
     * @param shopId the ID of the shop where the order is located.
     * @param controllerDTO the {@link ControllerDTO} containing order details.
     * @param state the new state to set for the order.
     * @return the updated {@link Order}.
     * @throws NoSuchElementException if the order or its state cannot be found.
     */
    private Order getNewState(String shopId, ControllerDTO controllerDTO, String state) {
        Optional<OrderDb> tempOrder = orderRepository.findByShopIdAndOrderId(shopId, controllerDTO.getOrderId());
        if (tempOrder.isPresent()) {
            OrderDb oldOrder = tempOrder.get();
            State lastState = oldOrder.getStates().get(oldOrder.getStates().size() - 1);
            if (lastState.getState().equals(states.getCancel()) || lastState.getState().equals(states.getComlete()))
                throw new NoSuchElementException(new StringBuilder().append("States '").append(states).append("' or '").append(states.getComlete()).append("' can't be changed!").toString());

            List<OrderPreps> preps = new ArrayList<>();
            for (int i = 0; i < controllerDTO.getConfirmedPreps().size(); i++) {
                if (state.equals(states.getCancel())) preps.add(addPrep(controllerDTO, lastState, i));
                else {
                    if (controllerDTO.getConfirmedPreps().get(i).isConfirmed()) {
                        preps.add(addPrep(controllerDTO, lastState, i));
                    }
                }
            }

            return Order.builder()
                    .agent(oldOrder.getAgent())
                    .phone(oldOrder.getPhone())
                    .idShop(oldOrder.getShop().getShopId())
                    .extidShop(oldOrder.getShopExtId())
                    .test(false)
                    .idOrder(controllerDTO.getOrderId())
                    .state(state)
                    .shipping(oldOrder.getShipping())
                    .data(preps)
                    .build();
        } else throw new NoSuchElementException(new StringBuilder()
                .append("Can't find order ")
                .append(controllerDTO.getOrderId())
                .append(" for shop ID: ")
                .append(shopId).toString());
    }

    /**
     * Adds a preparation item to an order.
     *
     * @param controllerDTO the {@link ControllerDTO} containing preparation details.
     * @param lastState the last state of the order.
     * @param k the index of the preparation item.
     * @return the {@link OrderPreps} representing the preparation item.
     */
    private OrderPreps addPrep(ControllerDTO controllerDTO, State lastState, int k) {
        String extId = "";
        for (PrepsInOrderDb prep: lastState.getPrepsInOrder()) {
            if (prep.getMorionId().equals(controllerDTO.getConfirmedPreps().get(k).getMorionId())) {
                extId = prep.getExtId();
                break;
            }
        }
        return OrderPreps.builder()
                        .id(controllerDTO.getConfirmedPreps().get(k).getMorionId())
                        .extId(extId)
                        .quant(controllerDTO.getConfirmedPreps().get(k).getQuant())
                        .price(controllerDTO.getConfirmedPreps().get(k).getPrice())
                .build();
    }


}
