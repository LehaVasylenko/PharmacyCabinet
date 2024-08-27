package com.orders.cabinet.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
/**
 * Configuration properties for Telegram module.
 * <p>
 * This class is used to bind properties defined in the application's configuration
 * files with the prefix {@code telegram.module}. It includes settings related to
 * Telegram integrations, such as URLs and paths for notifications.
 * </p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Component
@ConfigurationProperties(prefix = "telegram.module")
@Getter
@Setter
@Primary
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramProperties {
    /**
     * The base URL for the Telegram notificator app.
     */
    String url;

    /**
     * The endpoint for pinging the Telegram service.
     */
    String ping;

    /**
     * The path for pushing notifications about new orders.
     */
    String path;

    /**
     * The path for sending notifications about long term waiting orders.
     */
    String notificator;
}
