package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
 public class OrderPreps {
    String id;
    @JsonProperty("ext_id")
    String extId;
    Double quant;
    Double price;
}
