package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.DrugCache;
import com.orders.cabinet.model.db.order.PrepsInOrderDb;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link PrepsInOrderDb} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link PrepsInOrderDb} entities.
 * It leverages Spring Data JPA to generate the necessary implementation at runtime.</p>
 *
 * <p>By extending {@link JpaRepository}, it inherits several methods for working with {@link PrepsInOrderDb} persistence,
 * including methods for saving, deleting, and finding {@link PrepsInOrderDb} entities.</p>
 *
 * @see PrepsInOrderDb
 * @see JpaRepository
 *
 * @param <PrepsInOrderDb> the type of the entity to handle
 * @param <Long> the type of the entity's identifier
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
public interface PrepsInOrderRepository extends JpaRepository<PrepsInOrderDb, Long> {
}
