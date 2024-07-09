package com.orders.cabinet.model.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO {
    String phone;
    String time;
    String idOrder;
    String state;
    List<OrderPrepsDTO> data;
}
