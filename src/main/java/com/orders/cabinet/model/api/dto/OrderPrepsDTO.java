package com.orders.cabinet.model.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for List of drugs in order")
public class OrderPrepsDTO {
    @Schema(description = "Drug identifier in the Morion database", example = "7167")
    String morionId;
    @Schema(description = "Drug name", example = "Никоретте® Зимняя мята 2 мг резинка жевательная лечебная блистер , №30, McNeil")
    String drugName;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantity of drug in order", example = "1.0")
    Double quant;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Price of drug in order", example = "275.01")
    Double price;

    @Schema(description = "Flag used when confirming an order for the first time. Not used in further actions", example = "true")
    boolean confirmed;
}
