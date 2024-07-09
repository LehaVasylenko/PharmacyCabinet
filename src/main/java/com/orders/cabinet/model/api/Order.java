package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
   @JsonProperty("id_shop")
   String idShop;
   @JsonProperty("ext_id_shop")
   String extidShop;
   String phone;
   Boolean test;
   @JsonProperty("MsgFlags")
   Integer msgFlags;
   Long timestamp;
   String agent;
   @JsonProperty("id_order")
   String idOrder;
   String shipping;
   String state;
   String reason;
   String attribute;
   List<OrderPreps> data;
}

