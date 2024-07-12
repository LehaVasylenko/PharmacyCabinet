package com.orders.cabinet.event;

import com.orders.cabinet.model.db.LogEntry;
import com.orders.cabinet.service.LogEntryService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntityAuditListener {

    final LogEntryService service;
    @Autowired
    @Lazy
    public EntityAuditListener(LogEntryService service) {
        this.service = service;
    }

    @PrePersist
    public void onPrePersist(Object entity) {
        // Log or audit creation event
        log.info("Entity created: " + entity.toString());
        saveLog(entity, "CREATED");
    }

    private void saveLog(Object entity, String status) {
        service.saveLogEntryAsync(LogEntry
                .builder()
                .timestamp(LocalDateTime.now())
                .action(status)
                .message(entity.toString())
                .build());
    }

    @PreUpdate
    public void onPreUpdate(Object entity) {
        // Log or audit update event
        log.info("Entity updated: " + entity.toString());
        saveLog(entity, "UPDATED");
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        // Log or audit deletion event
        log.info("Entity deleted: " + entity.toString());
        saveLog(entity, "DELETED");
    }
}
