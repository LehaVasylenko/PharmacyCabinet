package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.order.OrderDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderDb, Long> {
    @Query("SELECT DISTINCT o " +
            "FROM OrderDb o " +
            "JOIN FETCH o.states s " +
            "JOIN FETCH s.prepsInOrder p " +
            "WHERE s.state = 'New' AND o.shop.shopId = :shopId " +
            "GROUP BY o.id " +
            "HAVING COUNT(DISTINCT s.state) = 1")
    Optional<List<OrderDb>> findOrdersWithOnlyNewStateForShop(@Param("shopId") String shopId);

    @Query("SELECT o FROM OrderDb o " +
            "JOIN FETCH o.shop s " +
            "WHERE s.shopId = :shopId AND o.orderId = :orderId")
    Optional<OrderDb> findByShopIdAndOrderId(@Param("shopId") String shopId, @Param("orderId") String orderId);

    @Query("SELECT o FROM OrderDb o JOIN FETCH o.shop WHERE SIZE(o.states) = 1")
    List<OrderDb> findOrdersWithOnlyOneState();
}
