package com.orders.cabinet.service;

import com.orders.cabinet.model.db.LogEntry;
import com.orders.cabinet.repository.LogEntryRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogEntryService {

    LogEntryRepository logEntryRepository;

    @Async
    public void saveLogEntryAsync(LogEntry logEntry) {
        logEntryRepository.saveLogEntry(logEntry);
    }
}
