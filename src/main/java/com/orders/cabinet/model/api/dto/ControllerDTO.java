package com.orders.cabinet.model.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Order manipulation Data Transfer Object")
public class ControllerDTO {
    @Schema(description = "Order ID", example = "240502-006208")
    String orderId;
    @Schema(description = "Field to describe a cancel reason. Field used to describe the reason for the failure. Used only with a method that sends status 'Canceled'", example = "The buyer did not have a prescription")
    String reason;
    @Schema(description = "List of drugs in order")
    List<OrderPrepsDTO> confirmedPreps;
}
