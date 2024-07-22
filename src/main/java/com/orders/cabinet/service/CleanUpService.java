package com.orders.cabinet.service;

import com.orders.cabinet.configuration.StatesProperties;
import com.orders.cabinet.mapper.ShopInfoCachRepositoryMapper;
import com.orders.cabinet.model.db.ShopInfoCache;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.CorpRepository;
import com.orders.cabinet.repository.OrderRepository;
import com.orders.cabinet.repository.ShopInfoCacheRepository;
import com.orders.cabinet.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Service for performing periodic cleanup tasks.
 *
 * <p>This service provides scheduled methods for cleaning up orders and updating shop information cache.</p>
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
@EnableScheduling
public class CleanUpService {
    OrderRepository orderRepository;
    StatesProperties statesProperties;
    CorpRepository corpRepository;
    ShopInfoCacheRepository shopInfoCacheRepository;
    AdminService adminService;

    /**
     * Cleans up orders based on their states and lifetimes.
     *
     * <p>This method is scheduled to run daily at 00:01. It deletes orders with specific states (complete or canceled)
     * and also removes expired orders based on their lifetimes. Additionally, it updates the shop information cache.</p>
     */
    //@Scheduled(cron = "0 */1 * * * *")// Testing feature each 1 minutes
    @Scheduled(cron = "0 1 0 * * *") // Runs every day at 00:01
    @Transactional
    public void cleanupOrders() {
        List<OrderDb> allOrders = orderRepository.findAllOrders();

        List<OrderDb> ordersToDelete = allOrders.stream()
                .filter(order -> order
                        .getStates()
                        .stream()
                        .anyMatch(state -> statesProperties.getComlete().equals(state.getState()) || statesProperties.getCancel().equals(state.getState())))
                .toList();

        if (!ordersToDelete.isEmpty()) {
            orderRepository.deleteAll(ordersToDelete);
            allOrders.removeAll(ordersToDelete);
            log.info("Deleted orders:\n{}", ordersToDelete.stream()
                    .map(OrderDb::getOrderId)
                    .collect(Collectors.joining("\n")));
        }

        List<OrderDb> expiredOrders = new ArrayList<>();
        for (OrderDb order: allOrders) {
            if (order.getStates().get(order.getStates().size() - 1).getState().equals(statesProperties.getConfirm())) {
                Integer lifetime = corpRepository
                        .findLifetimeByOrderIdAndShopId(order.getOrderId(), order.getShop().getShopId());
                log.info(lifetime.toString());

                LocalDateTime localDateTimeFromLastStateWithLifetime = order.getStates()
                        .get(order.getStates().size() - 1)
                        .getTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusHours(lifetime);

                LocalDateTime now = LocalDateTime.now();
                if (localDateTimeFromLastStateWithLifetime.isAfter(now)) expiredOrders.add(order);
            }

            if (order.getStates().get(order.getStates().size() - 1).getState().equals(statesProperties.getNeww())) {
                LocalDateTime localDateTimeFromLastStateWithLifetime = order.getStates()
                        .get(order.getStates().size() - 1)
                        .getTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .plusMinutes(40L);

                LocalDateTime now = LocalDateTime.now();
                if (localDateTimeFromLastStateWithLifetime.isAfter(now)) expiredOrders.add(order);
            }
        }
        if (!expiredOrders.isEmpty()) {
            orderRepository.deleteAll(expiredOrders);
            log.info("Deleted expired orders:\n{}", ordersToDelete.stream()
                    .map(OrderDb::getOrderId)
                    .collect(Collectors.joining("\n")));
        }

        updateShopInfoCache();
    }

    /**
     * Updates the shop information cache.
     *
     * <p>This method retrieves the current shop information from an external source and updates the local cache if
     * discrepancies are found.</p>
     */
    private void updateShopInfoCache() {
        List<ShopInfoCacheDTO> all = shopInfoCacheRepository
                .findAll()
                .stream()
                .map(ShopInfoCachRepositoryMapper.INSTANCE::toDto)
                .toList();
        for (ShopInfoCacheDTO shopInfo: all) {
            ShopInfoCacheDTO shopInfoCacheDTOResponseEntity = adminService.getShopInfoCacheDTOResponseEntity(shopInfo.getShopId());
            if (!shopInfo.equals(shopInfoCacheDTOResponseEntity)) {
                shopInfoCacheRepository.save(ShopInfoCachRepositoryMapper.INSTANCE.toModel(shopInfoCacheDTOResponseEntity));
            }
        }
    }


}
