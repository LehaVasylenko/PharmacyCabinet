package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.order.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {
    @Query("SELECT s FROM State s " +
            "WHERE s.state = 'New' AND s.shop.shopId = :shopId " +
            "GROUP BY s.order " +
            "HAVING COUNT(DISTINCT s.state) = 1")
    Optional<List<State>> findStatesWithOnlyNewStateForShop(@Param("shopId") String shopId);
}
