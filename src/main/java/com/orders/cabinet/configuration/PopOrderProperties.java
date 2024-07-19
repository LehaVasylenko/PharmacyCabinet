package com.orders.cabinet.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
/**
 * Configuration properties class for the Pop Order integration.
 * <p>
 * This class holds the properties required for configuring the integration with the Pop Order system.
 * It is populated with values from the application configuration (e.g., application.properties or application.yml)
 * based on the prefix "pop-order".
 * </p>
 * <p>
 * It includes properties such as the URL, pop-order path, update path, agent identifier, and rate settings.
 * </p>
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Component
@ConfigurationProperties(prefix = "pop-order")
@Getter
@Setter
@Primary
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PopOrderProperties {
    /**
     * The base URL for the Pop Order integration.
     * <p>
     * This URL is used to connect to the Pop Order service.
     * </p>
     */
    String url;
    /**
     * The path to /pop-order.
     */
    String pop;
    /**
     * The path to /upd-order.
     */
    String upd;
    /**
     * User-agent to header.
     */
    String agent;
    /**
     * Rate to ask pop-order about new orders
     */
    long rate;

}