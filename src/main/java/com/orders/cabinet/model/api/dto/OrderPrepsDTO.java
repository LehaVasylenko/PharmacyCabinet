package com.orders.cabinet.model.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderPrepsDTO {
    String morionId;
    String drugName;

    @NotNull
    @PositiveOrZero
    Double quant;

    @NotNull
    @PositiveOrZero
    Double price;

    boolean confirmed;
}
