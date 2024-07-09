package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrepsInShop {
    @JsonProperty("id_drug")
    String idDrug;
    String drugName;
    Integer quant;
    Double price;
    @JsonProperty("price_cntr")
    Double priceCntr;
    Integer pfactor;
}
