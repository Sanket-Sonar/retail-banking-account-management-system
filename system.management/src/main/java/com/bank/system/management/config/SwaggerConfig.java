package com.bank.system.management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Management API",
                version = "1.0",
                description = "JWT Authentication APIs"
        )
)
public class SwaggerConfig {
}
