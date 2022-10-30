package it.winter2223.bachelor.ak.backend.config;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
class SpringdocOpenAPIConfig {
}
