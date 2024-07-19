package com.orders.cabinet.model.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
/**
 * Data Transfer Object (DTO) for order manipulation.
 *
 * <p>This DTO is used to transfer data related to order operations, including order identification,
 * reasons for cancellation, and the list of confirmed drugs in the order.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Order manipulation Data Transfer Object")
public class ControllerDTO {
    /**
     * Unique identifier for the order.
     *
     * <p>This field represents the ID of the order in the system.</p>
     *
     * @example 240502-006208
     */
    @Schema(description = "Order ID", example = "240502-006208")
    String orderId;

    /**
     * Reason for order cancellation.
     *
     * <p>This field is used to describe the reason for the failure or cancellation of the order.
     * It is only applicable when the order status is 'Canceled'.</p>
     *
     * @example The buyer did not have a prescription
     */
    @Schema(description = "Field to describe a cancel reason. Field used to describe the reason for the failure. Used only with a method that sends status 'Canceled'", example = "The buyer did not have a prescription")
    String reason;

    /**
     * List of drugs in the order.
     *
     * <p>This field contains a list of {@link OrderPrepsDTO} objects representing the drugs
     * that have been confirmed in the order.</p>
     */
    @Schema(description = "List of drugs in order")
    List<OrderPrepsDTO> confirmedPreps;
}
