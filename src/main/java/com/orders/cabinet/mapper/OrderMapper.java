package com.orders.cabinet.mapper;

import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.api.OrderPreps;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.api.dto.OrderPrepsDTO;
import com.orders.cabinet.model.db.DrugCache;
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
import java.util.concurrent.ExecutionException;

/**
 * Mapper component for converting between various representations of orders and their preparations.
 *
 * <p>This class provides methods to map between {@link OrderDb}, {@link Order}, and their respective DTOs {@link OrderDTO}
 * and {@link OrderPrepsDTO}. It also handles the conversion of timestamps to formatted date strings and interacts with
 * {@link DrugNameService} to resolve drug names.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderMapper {
    /**
     * Service to retrieve drug names.
     */
    DrugNameService drugNameService;

    /**
     * Converts an {@link OrderDb} entity to an {@link OrderDTO}.
     *
     * <p>This method maps the fields from the {@link OrderDb} entity to the {@link OrderDTO} object,
     * including extracting preparation details and resolving drug names.</p>
     *
     * @param orderDb the {@link OrderDb} entity to be converted
     * @return the corresponding {@link OrderDTO} object
     */
    public OrderDTO DBToDTO(OrderDb orderDb) {
        return OrderDTO.builder()
                .idOrder(orderDb.getOrderId())
                .phone(orderDb.getPhone())
                .time(getTime(orderDb.getTimestamp()))
                .state(orderDb.getStates().get(orderDb.getStates().size() - 1).getState())
                .data(getPrersDtoListFromDB(orderDb
                        .getStates()
                        .get(orderDb.getStates().size() - 1)
                        .getPrepsInOrder()))
                .build();
    }

    /**
     * Converts an {@link Order} object to an {@link OrderDTO}.
     *
     * <p>This method maps the fields from the {@link Order} object to the {@link OrderDTO} object,
     * including converting preparation details and resolving drug names.</p>
     *
     * @param order the {@link Order} object to be converted
     * @return the corresponding {@link OrderDTO} object
     */
    public OrderDTO OrderToDto(Order order) {
        return OrderDTO.builder()
                .idOrder(order.getIdOrder())
                .phone(order.getPhone())
                .time(getTime(order.getTimestamp()))
                .state(order.getState())
                .data(getPrersDtoList(order.getData()))
                .build();
    }

    /**
     * Converts a list of {@link OrderPreps} to a list of {@link OrderPrepsDTO}.
     *
     * <p>This method maps each {@link OrderPreps} object to an {@link OrderPrepsDTO} object. It also
     * resolves drug names asynchronously using {@link DrugNameService}.</p>
     *
     * @param orderPreps the list of {@link OrderPreps} objects to be converted
     * @return the list of corresponding {@link OrderPrepsDTO} objects
     */
    private List<OrderPrepsDTO> getPrersDtoList (List<OrderPreps> orderPreps) {
        List<OrderPrepsDTO> result = new ArrayList<>();
        for (int i = 0; i < orderPreps.size(); i++) {

            DrugCache drugCache = null;
            try {
                drugCache = drugNameService
                        .getDrugName(orderPreps.get(i).getId())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                drugCache = new DrugCache(orderPreps.get(i).getId(), "Unknown Drug", "");
            }

            result.add(OrderPrepsDTO
                    .builder()
                            .morionId(orderPreps.get(i).getId())
                            .drugName(drugCache.getDrugName())
                            .drugLink(drugCache.getDrugLink())
                            .quant(orderPreps.get(i).getQuant())
                            .price(orderPreps.get(i).getPrice())
                    .build());
        }

        return result;
    }

    /**
     * Converts a list of {@link PrepsInOrderDb} to a list of {@link OrderPrepsDTO}.
     *
     * <p>This method maps each {@link PrepsInOrderDb} object to an {@link OrderPrepsDTO} object. It also
     * resolves drug names asynchronously using {@link DrugNameService}.</p>
     *
     * @param orderPreps the list of {@link PrepsInOrderDb} objects to be converted
     * @return the list of corresponding {@link OrderPrepsDTO} objects
     */
    private List<OrderPrepsDTO> getPrersDtoListFromDB (List<PrepsInOrderDb> orderPreps) {
        List<OrderPrepsDTO> result = new ArrayList<>();
        for (int i = 0; i < orderPreps.size(); i++) {

            DrugCache drugName = null;
            try {
                drugName = drugNameService
                        .getDrugName(orderPreps.get(i).getMorionId())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                drugName = new DrugCache(orderPreps.get(i).getMorionId(), "Unknown Drug", "");
            }

            result.add(OrderPrepsDTO
                    .builder()
                    .drugName(drugName.getDrugName())
                    .drugLink(drugName.getDrugLink())
                    .quant(orderPreps.get(i).getQuant())
                    .price(orderPreps.get(i).getPrice())
                    .build());
        }

        return result;
    }

    /**
     * Converts a Unix timestamp to a formatted date-time string.
     *
     * <p>This method converts the provided Unix timestamp to a formatted string representing the local date
     * and time. The format is "HH:mm:ss dd.MM.yyyy".</p>
     *
     * @param timestamp the Unix timestamp to be converted
     * @return the formatted date-time string
     */
    private String getTime(Long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        return dateTime.format(formatter);
    }
}
