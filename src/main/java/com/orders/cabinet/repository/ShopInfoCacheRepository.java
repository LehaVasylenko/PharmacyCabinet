package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.ShopInfoCache;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
/**
 * Repository interface for {@link ShopInfoCache} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link ShopInfoCache} entities.
 * It leverages Spring Data JPA to generate the necessary implementation at runtime.</p>
 *
 * <p>Custom queries are defined to perform specific operations such as truncating the shop_info_cache table.</p>
 *
 * @see ShopInfoCache
 * @see JpaRepository
 *
 * @param <ShopInfoCache> the type of the entity to handle
 * @param <String> the type of the entity's identifier
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
public interface ShopInfoCacheRepository extends JpaRepository<ShopInfoCache, String> {

    /**
     * Truncates the shop_info_cache table.
     *
     * <p>This method uses a native query to perform the truncate operation on the shop_info_cache table.
     * It is marked with {@link Modifying} and {@link Transactional} annotations to ensure the operation
     * is executed within a transaction and modifies the database state.</p>
     */
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE shop_info_cache", nativeQuery = true)
    void truncateTable();
}
