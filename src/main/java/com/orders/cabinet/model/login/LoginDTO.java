package com.orders.cabinet.model.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for Login")
public class LoginDTO {

    @NotEmpty(message = "Username can't be empty!")
    @Schema(description = "Morion identificator ", example = "123456789")
    String shopId;

    @NotEmpty(message = "Password can't be empty!")
    @Schema(description = "Password", example = "92308rugibrgneop3i2yrtg3")
    String password;

}
