package com.orders.cabinet.model.db;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "log_entries")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime timestamp;

    @Column(name = "action", length = 10)
    String action;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    @Column(name = "exception", columnDefinition = "TEXT")
    String exception;
}
