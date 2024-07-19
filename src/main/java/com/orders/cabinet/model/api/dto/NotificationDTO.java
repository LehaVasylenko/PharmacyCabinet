package com.orders.cabinet.model.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Data Transfer Object (DTO) for sending notifications to the Telegram application.
 *
 * <p>This DTO is used to transfer information about notifications, including the shop ID
 * and order ID that are relevant for sending notifications to Telegram.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDTO {

    /**
     * Identifier for the shop.
     *
     * <p>This field represents the unique ID of the shop that is associated with the notification.</p>
     */
    String shopId;

    /**
     * Identifier for the order.
     *
     * <p>This field represents the unique ID of the order that is associated with the notification.</p>
     */
    String orderId;
}
