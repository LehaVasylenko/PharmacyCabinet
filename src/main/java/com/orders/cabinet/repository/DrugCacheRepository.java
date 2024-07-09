package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.DrugCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugCacheRepository extends JpaRepository<DrugCache, String> {
}
