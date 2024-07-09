package com.orders.cabinet.mapper;

import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.api.OrderPreps;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.api.dto.OrderPrepsDTO;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.model.db.order.PrepsInOrderDb;
import com.orders.cabinet.service.DrugNameService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderMapper {

    DrugNameService drugNameService;
    public OrderDTO DBToDTO(OrderDb orderDb) {
        return OrderDTO.builder()
                .idOrder(orderDb.getOrderId())
                .phone(orderDb.getPhone())
                .time(getTime(orderDb.getTimestamp()))
                .state(orderDb.getStates().get(0).getState())
                .data(getPrersDtoListFromDB(orderDb
                        .getStates()
                        .get(orderDb.getStates().size() - 1)
                        .getPrepsInOrder()))
                .build();
    }

    public OrderDTO OrderToDto(Order order) {

        return OrderDTO.builder()
                .idOrder(order.getIdOrder())
                .phone(order.getPhone())
                .time(getTime(order.getTimestamp()))
                .state(order.getState())
                .data(getPrersDtoList(order.getData()))
                .build();
    }

    private List<OrderPrepsDTO> getPrersDtoList (List<OrderPreps> orderPreps) {
        List<OrderPrepsDTO> result = new ArrayList<>();
        for (int i = 0; i < orderPreps.size(); i++) {

            String drugName = null;
            try {
                drugName = drugNameService
                        .getDrugName(orderPreps.get(i).getId())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                drugName = "Unknown Drug";
            }

            result.add(OrderPrepsDTO
                    .builder()
                            .morionId(orderPreps.get(i).getId())
                            .drugName(drugName)
                            .quant(orderPreps.get(i).getQuant())
                            .price(orderPreps.get(i).getPrice())
                    .build());
        }

        return result;
    }

    private List<OrderPrepsDTO> getPrersDtoListFromDB (List<PrepsInOrderDb> orderPreps) {
        List<OrderPrepsDTO> result = new ArrayList<>();
        for (int i = 0; i < orderPreps.size(); i++) {

            String drugName = null;
            try {
                drugName = drugNameService
                        .getDrugName(orderPreps.get(i).getMorionId())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                drugName = "Unknown Drug";
            }

            result.add(OrderPrepsDTO
                    .builder()
                    .drugName(drugName)
                    .quant(orderPreps.get(i).getQuant())
                    .price(orderPreps.get(i).getPrice())
                    .build());
        }

        return result;
    }



    private String getTime(Long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd:MM:yyyy");
        return dateTime.format(formatter);
    }
}
