package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.ShopInfoCache;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShopInfoCacheRepository extends JpaRepository<ShopInfoCache, String> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE shop_info_cache", nativeQuery = true)
    void truncateTable();
}
