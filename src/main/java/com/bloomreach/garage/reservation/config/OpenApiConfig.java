package com.bloomreach.garage.reservation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**") // Customize the path according to your needs
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Garage Reservation API")
                        .version("v1")
                        .description("""
                                API for managing garage reservations. This API provides endpoints for creating,
                                viewing, updating, and deleting reservations for vehicle servicing in a garage. Users can book
                                appointments for different types of operations, check availability of time slots, and manage
                                employees and customers involved in the appointments. The API supports querying available
                                slots based on date, operation type, and employee availability, as well as handling different
                                types of garage closures and operational hours.
                                """)
                        .contact(new Contact()
                                .name("Support Team")
                                .url("http://support.example.com")
                                .email("support@example.com")))

                // The following security configuration is enabled to demonstrate how security would be applied.
                // Configure as necessary for production environments to handle authorization and token management.
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                );
    }
}
