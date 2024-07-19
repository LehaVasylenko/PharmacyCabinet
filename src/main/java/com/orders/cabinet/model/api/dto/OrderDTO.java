package com.orders.cabinet.model.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
/**
 * Data Transfer Object (DTO) for Order information.
 *
 * <p>This DTO is used to transfer detailed information about an order, including its
 * phone number, order ID, state, error message, and associated drug details.</p>
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for Order information")
public class OrderDTO {

    /**
     * Phone number of the user who made the order.
     *
     * <p>This field represents the contact number of the user who placed the order.</p>
     *
     * @example "380504302231"
     */
    @Schema(description = "Phone number of the user who made the order", example = "380504302231")
    String phone;

    /**
     * Time when the order was made.
     *
     * <p>This field indicates the exact time when the order was placed.</p>
     *
     * @example "14:30:21 01.10.3059"
     */
    @Schema(description = "Time when the order was made", example = "14:30:21 01.10.3059")
    String time;

    /**
     * Order ID.
     *
     * <p>This field represents the unique identifier for the order.</p>
     *
     * @example "59301001"
     */
    @Schema(description = "Order ID", example = "59301001")
    String idOrder;

    /**
     * State of the order.
     *
     * <p>This field represents the current status of the order. Allowed states are:
     * 'New', 'Confirmed', 'Canceled', 'Completed'.</p>
     *
     * @example "New"
     */
    @Schema(description = "State of order. Allowed states are: 'New', 'Confirmed', 'Canceled', 'Completed'", example = "New")
    String state;

    /**
     * Error message.
     *
     * <p>This field contains an error message if an exception occurred during order processing.</p>
     *
     * @example "Houston, we have problems."
     */
    @Schema(description = "Error message. For any exception if exist", example = "Houston, we have problems.")
    String errorMessage;

    /**
     * List of drugs in the order.
     *
     * <p>This field represents the list of drugs associated with the order.</p>
     */
    @Schema(description = "List of drugs in the order")
    List<OrderPrepsDTO> data;
}
