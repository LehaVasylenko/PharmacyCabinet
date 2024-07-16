package com.orders.cabinet.model.db.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer object for corporation info")
public class CorpDTO {
    @Schema(description = "Corporation ID. It is better to match data from https://api.geoapteka.com.ua/show-shops, but not mandatory", example = "9356")
    String corpId;
    @Schema(description = "Login for access to Booking and Sky-Net services. Have to match with DB Booking", example = "jfvneivbnonvienienek")
    String login;
    @Schema(description = "Password for access to Booking and Sky-Net services. Have to match with DB Booking", example = "9038765678908765rghjnbvhuowi76783")
    String password;
    @Schema(description = "Corporation name. It is better to match data from https://api.geoapteka.com.ua/show-shops, but not mandatory", example = "Kryzhopyl Pharmacy")
    String corpName;
    @Schema(description = "Order lifetime set in the database for a specific corporation. Have to match with DB Booking", example = "48")
    Integer lifeTime;
}
