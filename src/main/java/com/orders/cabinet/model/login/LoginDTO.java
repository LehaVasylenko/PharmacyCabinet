package com.orders.cabinet.model.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
/**
 * Data Transfer Object for handling login requests.
 *
 * <p>This class is used to encapsulate the login information required for user authentication.
 * It includes fields for the shop identifier and password, both of which are mandatory for
 * a successful login request. The fields are validated to ensure they are not empty.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for Login Request")
public class LoginDTO {

    /**
     * The Morion identifier for the shop.
     *
     * <p>This field is required and must not be empty. It represents the unique identifier for
     * the shop in the Morion system.</p>
     */
    @NotEmpty(message = "Username can't be empty!")
    @Schema(description = "Morion identificator ", example = "123456789")
    String shopId;

    /**
     * The password for the login request.
     *
     * <p>This field is required and must not be empty. It represents the password associated with
     * the shop identifier for authentication purposes.</p>
     */
    @NotEmpty(message = "Password can't be empty!")
    @Schema(description = "Password", example = "92308rugibrgneop3i2yrtg3")
    String password;

}
