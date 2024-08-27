package com.orders.cabinet.service;

import com.orders.cabinet.model.db.*;
import com.orders.cabinet.repository.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
/**
 * Service for managing log entries.
 *
 * <p>This service provides methods for asynchronously saving log entries to the repository.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogEntryService {

    LogEntryRepository logEntryRepository;

    /**
     * Asynchronously saves a log entry to the repository.
     *
     * <p>This method is executed asynchronously to improve performance and avoid blocking operations.</p>
     *
     * @param logEntry The {@link LogEntityEntry} instance to be saved.
     */
    @Async
    public void saveLogEntryAsync(LogEntityEntry logEntry) {
        logEntryRepository.saveLogEntry(logEntry);
    }
}
