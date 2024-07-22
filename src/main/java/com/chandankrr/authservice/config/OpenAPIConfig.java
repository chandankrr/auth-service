package com.chandankrr.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI authServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Authentication Service API")
                        .description("This API handles the authentication services for Budget Buddy, a mobile application designed for tracking expenses. It includes endpoints for user registration, login, token generation, and more.")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")));
    }
}
