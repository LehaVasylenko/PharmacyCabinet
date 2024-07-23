package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.DrugCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for {@link DrugCache} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link DrugCache} entities.
 * It leverages Spring Data JPA to generate the necessary implementation at runtime.</p>
 *
 * <p>By extending {@link JpaRepository}, it inherits several methods for working with {@link DrugCache} persistence,
 * including methods for saving, deleting, and finding {@link DrugCache} entities.</p>
 *
 * @see DrugCache
 * @see JpaRepository
 *
 * @param <DrugCache> the type of the entity to handle
 * @param <String> the type of the entity's identifier
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
public interface DrugCacheRepository extends JpaRepository<DrugCache, String> {

    @Query("SELECT d.drugId FROM DrugCache d")
    List<String> findAllDrugIds();
    @Query("SELECT d FROM DrugCache d WHERE d.drugId IN :drugIds")
    List<DrugCache> findAllByDrugIdIn(@Param("drugIds") List<String> drugIds);

}
