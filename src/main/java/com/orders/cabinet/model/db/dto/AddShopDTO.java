package com.orders.cabinet.model.db.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddShopDTO {
    @NotEmpty
    String shopId;
    @NotEmpty
    String password;
    @NotEmpty
    String corpId;
}
