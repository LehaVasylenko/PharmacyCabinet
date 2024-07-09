package com.orders.cabinet.model.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControllerDTO {
    String orderId;
    String reason;
    List<OrderPrepsDTO> confirmedPreps;
}
