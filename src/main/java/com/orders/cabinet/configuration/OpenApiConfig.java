package com.orders.cabinet.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Configuration class for setting up OpenAPI documentation for the Cabinet API.
 * <p>
 * This class defines the OpenAPI specification for the API, including the title, version,
 * and description of the API. It is used to generate interactive API documentation
 * for the endpoints exposed by the application.
 * </p>
 * <p>
 * The OpenAPI documentation can be accessed through the Swagger UI or other OpenAPI-compatible
 * tools to provide a user-friendly interface for exploring and testing the API.
 * </p>
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates a bean for the OpenAPI configuration.
     * <p>
     * This method configures the OpenAPI documentation with the title, version,
     * and description of the API. It is used by the Spring context to set up
     * the OpenAPI documentation for the application.
     * </p>
     *
     * @return an {@link OpenAPI} object configured with the API information.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Cabinet API")
                        .version("1.0")
                        .description("Personal cabinet for dumb drugstores, who can't create a proper integration with booking"));
    }

}

