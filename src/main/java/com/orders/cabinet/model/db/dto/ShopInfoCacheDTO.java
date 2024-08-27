package com.orders.cabinet.model.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for Login Response")
public class ShopInfoCacheDTO {
    @JsonProperty("id")
    @Schema(description = "Pharmacy ID address in the Morion database", example = "124586")
    String shopId;

    @JsonProperty("id_corp")
    @Schema(description = "Corporation ID in the Morion database", example = "9268")
    String corpId;

    @JsonProperty("corp_ua")
    @Schema(description = "Corporation Name", example = "Kryzhopyl Farmacy")
    String corpName;

    @JsonProperty("name")
    @Schema(description = "Pharmacy name", example = "Apteka 11")
    String name;

    @JsonProperty("mark_ua")
    @Schema(description = "Pharmacy mark", example = "Kryzhopyl Vitaminkas")
    String mark;

    @JsonProperty("addr_area_ua")
    @Schema(description = "Area where the pharmacy is located", example = "Kyivska")
    String area;

    @JsonProperty("addr_city_ua")
    @Schema(description = "City where the pharmacy is located", example = "Kyiv")
    String city;

    @JsonProperty("addr_street_ua")
    @Schema(description = "Street where the pharmacy is located", example = "1 Bankova str")
    String street;

    @JsonProperty("up_date")
    @Schema(description = "Last time price archieved. Deprecated", example = "Deprecated")
    String update;

    @JsonProperty("open_hours")
    @Schema(description = "Pharmacy opening hours", example = "Mo-Fr 07:45-21:15 Sa 07:45-20:15 Su 08:45-20:15")
    String openHours;

    @Schema(description = "Error message if any exists. Other fields will be ignored", example = "Houston, we have problems.")
    String errorMessage;
}
