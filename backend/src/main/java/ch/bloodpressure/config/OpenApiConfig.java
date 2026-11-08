package ch.bloodpressure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Saliou Condé",
                        email = "saliou-conde@gmx.de"
                ),
                description = "Bloodpressure App Backend",
                title = "Bloodpressure App Backend",
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:9999"
                ),
                @Server(
                        description = "Production ENV",
                        url = "http://139.162.135.188:9999"
                )
        },
        // Aktiviert den Schutz global für die gesamte Swagger-UI
        security = {
                @SecurityRequirement(name = "BearerAuth")
        }
)
// Definiert das eigentliche JWT-Sicherheitskonzept für OpenAPI
@SecurityScheme(
        name = "BearerAuth",
        description = "Füge dein JWT-Token hier ein (ohne das Wort 'Bearer' davor)",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
