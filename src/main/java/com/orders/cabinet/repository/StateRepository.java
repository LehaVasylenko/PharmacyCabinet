package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.order.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for {@link State} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link State} entities.
 * It leverages Spring Data JPA to generate the necessary implementation at runtime.</p>
 *
 * <p>Custom queries are defined to perform specific operations such as finding states with only new states for a specific shop.</p>
 *
 * @see State
 * @see JpaRepository
 *
 * @param <State> the type of the entity to handle
 * @param <Long> the type of the entity's identifier
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
public interface StateRepository extends JpaRepository<State, Long> {

    /**
     * Finds states with only 'New' state for a specific shop.
     *
     * <p>This query selects states where the state is 'New' and the shop ID matches the provided parameter.
     * It groups the results by order and filters to include only those orders with a single distinct state.</p>
     *
     * @param shopId the ID of the shop
     * @return an {@link Optional} containing a list of states with only 'New' state for the specified shop, or {@link Optional#empty()} if none found
     */
    @Query("SELECT s FROM State s " +
            "WHERE s.state = 'New' AND s.shop.shopId = :shopId " +
            "GROUP BY s.order " +
            "HAVING COUNT(DISTINCT s.state) = 1")
    Optional<List<State>> findStatesWithOnlyNewStateForShop(@Param("shopId") String shopId);
}
