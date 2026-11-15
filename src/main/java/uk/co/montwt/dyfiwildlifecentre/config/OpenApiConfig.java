package uk.co.montwt.dyfiwildlifecentre.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Dyfi Wildlife Centre API",
                version = "2.0",
                description = "Visitor map POIs, two-way OpenStreetMap/OSRM directions, live webcams, wildlife sightings, and staff admin endpoints for Cors Dyfi Nature Reserve.",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class OpenApiConfig {
}
