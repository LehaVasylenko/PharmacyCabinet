package com.orders.cabinet.model.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orders.cabinet.model.db.order.OrderDb;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopsDTO {
    @NotEmpty
    String shopId;
    @NotEmpty
    String password;
    Date lastLogin;
    Boolean loggedIn;
    String corpId;
    @JsonIgnore
    List<OrderDb> orders;
}
