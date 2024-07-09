package com.orders.cabinet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.cabinet.configuration.PopOrderProperties;
import com.orders.cabinet.event.OrderReceivedEvent;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    String path = "/upd-order";
    Integer OK = 201;
    String CANCEL = "Canceled";
    String COMPLETED = "Completed";
    String CONFIRMED = "Confirmed";
    Map<String, List<Order>> orderMap = new HashMap<>();
    OrderWriterService orderWriterService;

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

    @Async
    public CompletableFuture<List<OrderDTO>> getOrdersWithOnlyNewStateByShopId(String shopId) {
        if (orderMap.get(shopId) != null) {
            List<Order> orders = new ArrayList<>(orderMap.get(shopId));
            orderMap.remove(shopId);

            return CompletableFuture.supplyAsync(() ->
                    orders.stream().map(mapper::OrderToDto).collect(Collectors.toList())
            );
        } else {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }


    @Async
    public CompletableFuture<?> confirmOrder(String shopId, ControllerDTO controllerDto) {
        //try {
            Order newOrder = getNewState(shopId, controllerDto, CONFIRMED);

            doPartyHard(newOrder);
            saveIt(newOrder);
            return CompletableFuture.completedFuture(CONFIRMED);
//        } catch (Exception e) {
//            return CompletableFuture.failedFuture(e);
//        }
    }

    @Async
    public CompletableFuture<?> completeOrder(String shopId, ControllerDTO controllerDto) {
        try {
            Order newOrder = getNewState(shopId, controllerDto, COMPLETED);

            doPartyHard(newOrder);
            saveIt(newOrder);

            return CompletableFuture.completedFuture(COMPLETED);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<?> cancelOrder(String shopId, ControllerDTO controllerDto) {
        try {
            Order newOrder = getNewState(shopId, controllerDto, CANCEL);
            newOrder.setReason(controllerDto.getReason());

            doPartyHard(newOrder);
            saveIt(newOrder);

            return CompletableFuture.completedFuture(CANCEL);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private void saveIt(Order newOrder) {
        newOrder.setTimestamp(Instant.now().getEpochSecond());
        orderWriterService.saveOrders(new Order[]{newOrder});
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

    private void doPartyHard(Order newOrder) {
        Optional<Corp> corp = shopRepository.findCorpByShopId(newOrder.getIdShop());
        if (corp.isPresent()) {
            String response = "";
            do {
                try {
                    String url = properties.getUrl() + path;
                    String requestBodyJson = objectMapper.writeValueAsString(newOrder);
                    HttpEntity<String> entity = new HttpEntity<>(requestBodyJson,
                            getHttpHeaders(corp.get().getLogin(), corp.get().getPassword()));
                    response = restTemplate
                            .exchange(url, HttpMethod.POST, entity, String.class)
                            .getStatusCode()
                            .toString();
                    log.info(response + ":\n" + requestBodyJson);

                } catch (HttpClientErrorException ex) {
                    log.error(ex.getStatusCode() + ": " + ex.getMessage());
                } catch (JsonProcessingException ex) {
                    log.error(ex.getMessage());
                }
            } while (response.equals("200"));
        } else {
            log.error("Can't be sent: {}", newOrder.toString());
        }
    }

    private Order getNewState(String shopId, ControllerDTO controllerDTO, String state) {
        Optional<OrderDb> tempOrder = orderRepository.findByShopIdAndOrderId(shopId, controllerDTO.getOrderId());
        if (tempOrder.isPresent()) {
            OrderDb oldOrder = tempOrder.get();
            State lastState = oldOrder.getStates().get(oldOrder.getStates().size() - 1);
            if (lastState.getState().equals(CANCEL) || lastState.getState().equals(COMPLETED))
                throw new NoSuchElementException("States '" + CANCEL + "' or '" + COMPLETED + "' can't be changed!");

            List<OrderPreps> preps = new ArrayList<>();
            for (int i = 0; i < controllerDTO.getConfirmedPreps().size(); i++) {
                if (state.equals(CANCEL)) preps.add(addPrep(controllerDTO, lastState, i));
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
        } else throw new NoSuchElementException("Can't find order " + controllerDTO.getOrderId() + " for shop ID: " + shopId);
    }

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
