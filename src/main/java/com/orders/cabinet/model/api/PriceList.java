package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceList {
    @JsonProperty("id_drug")
    String drugId;
    String drugName;
    String drugLink;
    String quant;
    String price;
    @JsonProperty("price_cntr")
    String priceCntr;
    Integer pfactor;
    String errorMessage;
}
