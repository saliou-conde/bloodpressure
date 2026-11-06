package ch.bloodpressure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
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
        }
)
public class OpenApiConfig {
}
