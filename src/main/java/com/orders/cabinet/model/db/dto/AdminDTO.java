package com.orders.cabinet.model.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orders.cabinet.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for administrator")
public class AdminDTO {
    @Schema(description = "username. Have to be unique")
    String username;
    @Schema(description = "password. You can't forget it")
    String password;
    @JsonIgnore
    Role role;
}
