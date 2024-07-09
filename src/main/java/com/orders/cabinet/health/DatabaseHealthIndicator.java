package com.orders.cabinet.health;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseHealthIndicator implements HealthIndicator {

    JdbcTemplate jdbcTemplate;

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
