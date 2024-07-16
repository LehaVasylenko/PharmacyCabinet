package com.orders.cabinet.model.db.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for Shop")
public class AddShopDTO {
    @NotEmpty
    @Schema(description = "Shop Id. Have to match with data from https://api.geoapteka.com.ua/show-shops", example = "12345678")
    String shopId;

    @NotEmpty
    @Schema(description = "Password. Needed to access the pharmacy cabinet. Could be anything", example = "qwerty12345")
    String password;

    @NotEmpty
    @Schema(description = "ID of the corporation that owns the current pharmacy. Must match the ID of the previously entered corporation", example = "3456")
    String corpId;
}
