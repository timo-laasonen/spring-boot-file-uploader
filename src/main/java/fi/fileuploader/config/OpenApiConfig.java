package fi.fileuploader.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Fileuploader App",
        description = "Fileuploader OpenAPI",
        version = "1.0.0"
    ),
    servers = @Server(url = "http://localhost:8080")
)
public class OpenApiConfig {
}
