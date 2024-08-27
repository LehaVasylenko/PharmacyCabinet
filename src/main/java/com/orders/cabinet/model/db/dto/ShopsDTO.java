package com.orders.cabinet.model.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.orders.cabinet.model.db.order.OrderDb;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "orders")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopsDTO {
    @NotEmpty
    String shopId;
    @NotEmpty
    String password;
    Date lastLogin;
    Boolean loggedIn;
    String corpId;
    @Schema(description = "Error message if any exists", example = "Houston, we have problems.")
    String errorMessage;
    @JsonIgnore
    List<OrderDb> orders;
}
