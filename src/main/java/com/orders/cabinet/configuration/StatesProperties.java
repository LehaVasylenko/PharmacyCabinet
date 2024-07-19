package com.orders.cabinet.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
/**
 * Configuration properties for managing states in the application.
 * <p>
 * This class maps properties from the application configuration with the prefix "state"
 * to fields in this class. It is used to hold configuration values related to various states
 * in the application.
 * </p>
 * <p>
 * The following state-related properties are supported:
 * <ul>
 *     <li>cancel: Represents the state for cancellation.</li>
 *     <li>confirm: Represents the state for confirmation.</li>
 *     <li>complete: Represents the state for completion.</li>
 *     <li>neww: Represents the state for new entries.</li>
 * </ul>
 * </p>
 * <p>
 * This class is annotated with {@link Component} to be registered as a Spring Bean,
 * {@link ConfigurationProperties} to bind the properties with the "state" prefix,
 * and {@link Primary} to indicate that it is the primary bean of its type.
 * </p>
 * <p>
 * The statuses that an order can receive during the business cycle are stored here.
 * In case something suddenly changes, you won’t have to lie down and change hard-coded statuses for all service classes
 * </p>
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Component
@ConfigurationProperties(prefix = "state")
@Getter
@Setter
@Primary
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatesProperties {
    /**
     * State for cancellation.
     */
    String cancel;

    /**
     * State for confirmation.
     */
    String confirm;

    /**
     * State for completion.
     */
    String comlete;

    /**
     * State for new entries.
     */
    String neww;
}
