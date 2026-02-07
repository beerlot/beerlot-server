package com.beerlot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPIConfig() {
        return new OpenAPI()
                .info(new Info().title("BeerLot API")
                        .description("BeerLot API Document")
                        .version("v1.1.0")
                        //.license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}
