package com.orders.cabinet.model.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopInfoCacheDTO {
    @JsonProperty("id")
    String shopId;

    @JsonProperty("id_corp")
    String corpId;

    @JsonProperty("corp_ua")
    String corpName;

    @JsonProperty("name")
    String name;

    @JsonProperty("mark_ua")
    String mark;

    @JsonProperty("addr_area_ua")
    String area;

    @JsonProperty("addr_city_ua")
    String city;

    @JsonProperty("addr_street_ua")
    String street;

    @JsonProperty("up_date")
    String update;

    @JsonProperty("open_hours")
    String openHours;

}
