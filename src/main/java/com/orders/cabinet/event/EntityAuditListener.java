package com.orders.cabinet.event;

import com.orders.cabinet.model.db.*;
import com.orders.cabinet.service.LogEntryService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
/**
 * EntityAuditListener is an event listener for auditing entity lifecycle events.
 *
 * <p>This class listens for entity lifecycle events such as creation, update, and deletion
 * and logs these events using the {@link LogEntryService}. It utilizes JPA lifecycle callback
 * methods to perform actions before persisting, updating, or removing entities.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityAuditListener {

    final LogEntryService service;

    /**
     * Constructs a new {@code EntityAuditListener} with the specified {@link LogEntryService}.
     *
     * @param service the {@link LogEntryService} used to save log entries
     */
    @Autowired
    @Lazy
    public EntityAuditListener(LogEntryService service) {
        this.service = service;
    }

    /**
     * Callback method invoked before an entity is persisted.
     *
     * <p>This method logs the creation of an entity and saves a log entry with the action
     * status set to "CREATED".</p>
     *
     * @param entity the entity that is about to be persisted
     */
    @PrePersist
    public void onPrePersist(Object entity) {
        // Log or audit creation event
        log.info("Entity created: " + entity.toString());
        saveLog(entity, "CREATED");
    }

    /**
     * Saves a log entry with the specified status.
     *
     * @param entity the entity associated with the log entry
     * @param status the status of the action (e.g., "CREATED", "UPDATED", "DELETED")
     */
    private void saveLog(Object entity, String status) {
        service.saveLogEntryAsync(LogEntityEntry
                .builder()
                .timestamp(LocalDateTime.now())
                .action(status)
                .message(entity.toString())
                .build());
    }

    /**
     * Callback method invoked before an entity is updated.
     *
     * <p>This method logs the update of an entity and saves a log entry with the action
     * status set to "UPDATED".</p>
     *
     * @param entity the entity that is about to be updated
     */
    @PreUpdate
    public void onPreUpdate(Object entity) {
        // Log or audit update event
        log.info("Entity updated: " + entity.toString());
        saveLog(entity, "UPDATED");
    }

    /**
     * Callback method invoked before an entity is removed.
     *
     * <p>This method logs the deletion of an entity and saves a log entry with the action
     * status set to "DELETED".</p>
     *
     * @param entity the entity that is about to be removed
     */
    @PreRemove
    public void onPreRemove(Object entity) {
        // Log or audit deletion event
        log.info("Entity deleted: " + entity.toString());
        saveLog(entity, "DELETED");
    }
}
