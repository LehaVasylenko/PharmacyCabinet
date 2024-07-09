package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.order.PrepsInOrderDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrepsInOrderRepository extends JpaRepository<PrepsInOrderDb, Long> {
}
