package com.orders.cabinet.model.db;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
/**
 * Represents a log entry in the system.
 *
 * <p>This entity class maps to the "log_entries" table in the database and records log information
 * including timestamps, actions performed, messages, and any exceptions encountered.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "log_entries")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogEntry {

    /**
     * The unique identifier for the log entry.
     *
     * <p>This field is used as the primary key for the "log_entries" table and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The timestamp when the log entry was created.
     *
     * <p>This field is automatically set to the current timestamp if not provided.</p>
     * <p>This field cannot be null.</p>
     */
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime timestamp;

    /**
     * The action performed that is being logged.
     *
     * <p>This field stores a brief description of the action, with a maximum length of 10 characters.</p>
     */
    @Column(name = "action", length = 10)
    String action;

    /**
     * A message providing details about the log entry.
     *
     * <p>This field can contain a detailed message or description and is stored as text.</p>
     */
    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    /**
     * Any exception encountered during the action being logged.
     *
     * <p>This field can contain stack trace or error details related to an exception and is stored as text.</p>
     */
    @Column(name = "exception", columnDefinition = "TEXT")
    String exception;
}
