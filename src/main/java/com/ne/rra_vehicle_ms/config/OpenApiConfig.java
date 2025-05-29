package com.ne.rra_vehicle_ms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Rwanda Government ERP Team",
                        email = "erp@gov.rw",
                        url = "https://gov.rw"
                ),
                description = "Rwanda Government Enterprise Resource Planning (ERP) System API. This system manages employee information, employment details, payroll processing, deductions, and messaging.",
                title = "Rwanda Government ERP System API",
                version = "1.0.0",
                license = @License(
                        name = "Apache License 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                termsOfService = "https://gov.rw/terms-of-service"
        ),
        servers = {
                @Server(
                        description = "Development Environment",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Staging Environment",
                        url = "https://staging-api.erp.gov.rw"
                ),
                @Server(
                        description = "Production Environment",
                        url = "https://api.erp.gov.rw"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication token. Please add 'Bearer ' prefix before your token.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
