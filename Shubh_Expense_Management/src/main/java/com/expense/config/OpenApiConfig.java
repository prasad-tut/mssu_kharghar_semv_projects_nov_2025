package com.expense.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the Expense Management System API.
 * Configures Swagger UI documentation with API information, security schemes, and servers.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Expense Management System API",
                version = "1.0.0",
                description = "REST API for managing business expenses, including expense tracking, " +
                        "categorization, receipt management, approval workflows, and reporting. " +
                        "Built with Spring Boot and secured with JWT authentication.",
                contact = @Contact(
                        name = "Expense Management Team",
                        email = "support@expensemanagement.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development Server",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production Server",
                        url = "https://api.expensemanagement.com"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication token. Obtain token by calling /api/auth/login endpoint.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
