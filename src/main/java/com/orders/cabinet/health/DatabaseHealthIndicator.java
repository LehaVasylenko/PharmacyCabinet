package com.orders.cabinet.health;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Custom {@link HealthIndicator} implementation for checking the health of the database.
 *
 * <p>This component implements the {@link HealthIndicator} interface to provide custom
 * health checks for the database. It uses {@link JdbcTemplate} to execute a simple query to
 * determine if the database connection is functional.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseHealthIndicator implements HealthIndicator {

    JdbcTemplate jdbcTemplate;

    /**
     * Performs the health check of the database.
     *
     * <p>This method executes a simple query to check the database connection. If the query
     * returns the expected result, it indicates that the database connection is healthy.
     * Otherwise, it reports the connection as unhealthy. In case of an exception, the method
     * reports the database connection as down.</p>
     *
     * @return a {@link Health} object representing the current health status of the database
     */
    @Override
    public Health health() {
        try {
            String queryResult = jdbcTemplate.queryForObject("SELECT 1", String.class);
            if ("1".equals(queryResult)) {
                return Health.up().withDetail("message", "Database connection is healthy").build();
            } else {
                return Health.down().withDetail("message", "Database connection returned unexpected result").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("message", "Database connection is down").build();
        }
    }
}
