package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.order.OrderDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for {@link OrderDb} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link OrderDb} entities.
 * It leverages Spring Data JPA to generate the necessary implementation at runtime.</p>
 *
 * <p>Custom queries are defined to retrieve orders based on various criteria such as shop ID, order ID,
 * and orders with only one state.</p>
 *
 * @see OrderDb
 * @see JpaRepository
 *
 * @param <OrderDb> the type of the entity to handle
 * @param <Long> the type of the entity's identifier
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
public interface OrderRepository extends JpaRepository<OrderDb, Long> {

    /**
     * Finds all orders and fetches their states and shop details.
     *
     * @return a list of all orders with their states and shop details
     */
    @Query("SELECT o " +
            "FROM OrderDb o " +
            "JOIN FETCH o.states s " +
            "JOIN FETCH o.shop sh")
    List<OrderDb> findAllOrders();

    /**
     * Finds an order by shop ID and order ID and fetches the shop details.
     *
     * @param shopId the ID of the shop
     * @param orderId the ID of the order
     * @return an optional containing the order if found, or empty if not found
     */
    @Query("SELECT o FROM OrderDb o " +
            "JOIN FETCH o.shop s " +
            "WHERE s.shopId = :shopId AND o.orderId = :orderId")
    Optional<OrderDb> findByShopIdAndOrderId(@Param("shopId") String shopId, @Param("orderId") String orderId);

    /**
     * Finds orders that have only one state.
     *
     * @return a list of orders with only one state
     */
    @Query("SELECT o FROM OrderDb o JOIN FETCH o.shop JOIN FETCH o.states s WHERE SIZE(o.states) = 1")
    List<OrderDb> findOrdersWithOnlyOneState();

    /**
     * Finds orders by shop ID and fetches the shop details.
     *
     * @param shopId the ID of the shop
     * @return a list of orders for the specified shop
     */
    @Query("SELECT o FROM OrderDb o " +
            "JOIN FETCH o.shop s " +
            "WHERE s.shopId = :shopId")
    List<OrderDb> findByShopId(@Param("shopId") String shopId);
}
