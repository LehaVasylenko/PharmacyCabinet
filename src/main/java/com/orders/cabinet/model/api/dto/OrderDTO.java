package com.orders.cabinet.model.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for Order information")
public class OrderDTO {
    @Schema(description = "Phone number of the user who made the order", example = "380504302231")
    String phone;
    @Schema(description = "Time when the order was made", example = "14:30:21 01.10.3059")
    String time;
    @Schema(description = "Order ID", example = "59301001")
    String idOrder;
    @Schema(description = "State of order. Allowed states are: 'New', 'Confirmed', 'Canceled', 'Completed'", example = "New")
    String state;
    @Schema(description = "List of drugs in the order")
    List<OrderPrepsDTO> data;
}
