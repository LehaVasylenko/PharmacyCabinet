package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.LogEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class LogEntryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveLogEntry(LogEntry logEntry) {
        entityManager.persist(logEntry);
    }
}
