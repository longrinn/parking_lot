package com.endava.internship.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                description = "OpenApi documentation for Parking Lot app",
                title = "Parking Lot specification",
                version = "1.0"
        )
)
interface OpenApiConfig {
}
