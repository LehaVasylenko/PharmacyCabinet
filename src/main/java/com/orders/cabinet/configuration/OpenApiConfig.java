package com.orders.cabinet.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 03.06.2024
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Cabinet API")
                        .version("1.0")
                        .description("Personal cabinet for dumb drugstores, who can't create a proper integration with booking"));
    }

}

