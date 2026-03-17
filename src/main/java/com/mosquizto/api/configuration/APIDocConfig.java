package com.mosquizto.api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class APIDocConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI(
            @Value("${open-api.service.title}") String title,
            @Value("${open-api.service.version}") String version,
            @Value("${open-api.service.server-url}") String serverUrl,
            @Value("${open-api.service.server-name}") String serverName) {
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description(serverName)))
                .info(new Info()
                        .title(title)
                        .description("Mosquizto Backend API - Full documentation for all endpoints")
                        .version(version)
                        .contact(new Contact().name("Mosquizto Team"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT access token here. Example: eyJhbGci...")));
    }
}
