package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

/**
 * Repository class for {@link LogEntityEntry} entity.
 *
 * <p>This class provides methods to perform CRUD operations for {@link LogEntityEntry} entities.
 * It uses {@link EntityManager} to interact with the persistence context.</p>
 *
 * <p>Annotated with {@link Repository} and {@link Transactional} to indicate that
 * it is a repository bean and its methods should be executed within a transaction context.</p>
 *
 * @see LogEntityEntry
 * @see EntityManager
 * @see PersistenceContext
 * @see Repository
 * @see Transactional
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Repository
@Transactional
public class LogEntryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a {@link LogEntityEntry} entity to the database.
     *
     * @param logEntry the log entry to save
     */
    public void saveLogEntry(LogEntityEntry logEntry) {
        entityManager.persist(logEntry);
    }
}
