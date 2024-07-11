package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.ShopInfoCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopInfoCacheRepository extends JpaRepository<ShopInfoCache, String> {
}
