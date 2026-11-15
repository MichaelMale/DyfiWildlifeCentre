package uk.co.montwt.dyfiwildlifecentre.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.montwt.dyfiwildlifecentre.config.WebcamProperties;
import uk.co.montwt.dyfiwildlifecentre.model.WildlifeSighting;
import uk.co.montwt.dyfiwildlifecentre.security.WebSecurityConfig;
import uk.co.montwt.dyfiwildlifecentre.service.DirectionsService;
import uk.co.montwt.dyfiwildlifecentre.service.WebcamService;
import uk.co.montwt.dyfiwildlifecentre.service.WildlifeSightingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {DirectionsController.class, WebcamController.class, SightingController.class})
@Import({WebSecurityConfig.class, WebcamProperties.class})
public class DirectionsAndWebcamControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DirectionsService directionsService;

    @MockitoBean
    private WebcamService webcamService;

    @MockitoBean
    private WildlifeSightingService wildlifeSightingService;

    @Test
    void directionsEndpointIsPublicAndReturnsRouteJson() throws Exception {
        DirectionsService.DirectionsResult sample = new DirectionsService.DirectionsResult(
                "SY20 8TG",
                "SY20 8TG",
                52.5923,
                -3.8548,
                "driving",
                3.2,
                7L,
                false,
                List.of(new double[]{52.568774, -3.918031}, new double[]{52.5923, -3.8548}),
                List.of("Head east onto A487 (3.2 mi)"),
                List.of(),
                "https://www.openstreetmap.org/directions",
                "https://www.traveline.cymru/"
        );
        when(directionsService.getDirections("SY20 8TG", "driving", "fromCentre")).thenReturn(sample);

        mockMvc.perform(get("/directions").param("destination", "SY20 8TG").param("mode", "driving"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("driving"))
                .andExpect(jsonPath("$.distanceMiles").value(3.2))
                .andExpect(jsonPath("$.steps[0]").value("Head east onto A487 (3.2 mi)"));
    }

    @Test
    void directionsEndpointSupportsToCentreDirection() throws Exception {
        DirectionsService.DirectionsResult sample = new DirectionsService.DirectionsResult(
                "SY20 8TG",
                "Dyfi Wildlife Centre",
                52.568774,
                -3.918031,
                "driving",
                3.2,
                7L,
                false,
                List.of(new double[]{52.5923, -3.8548}, new double[]{52.568774, -3.918031}),
                List.of("Arrive at destination"),
                List.of(),
                "https://www.openstreetmap.org/directions",
                "https://www.traveline.cymru/",
                "toCentre",
                "SY20 8TG",
                52.5923,
                -3.8548,
                "SY20 8TG"
        );
        when(directionsService.getDirections("SY20 8TG", "driving", "toCentre")).thenReturn(sample);

        mockMvc.perform(get("/directions")
                        .param("destination", "SY20 8TG")
                        .param("mode", "driving")
                        .param("direction", "toCentre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direction").value("toCentre"))
                .andExpect(jsonPath("$.originLabel").value("SY20 8TG"))
                .andExpect(jsonPath("$.destinationLabel").value("Dyfi Wildlife Centre"));
    }

    @Test
    void directionsEndpointReturnsBadRequestOnInvalidDestination() throws Exception {
        when(directionsService.getDirections("???", "driving", "fromCentre"))
                .thenThrow(new IllegalArgumentException("Could not find destination: ???"));

        mockMvc.perform(get("/directions").param("destination", "???").param("mode", "driving"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Could not find destination: ???"));
    }

    @Test
    void webcamsAndSightingsEndpointsArePublic() throws Exception {
        when(webcamService.getActivePublicFeeds()).thenReturn(new WebcamProperties().getWebcams());

        WildlifeSighting sighting = new WildlifeSighting();
        sighting.setId(5L);
        sighting.setSpecies("Osprey");
        sighting.setSpeciesCy("Gwalch y Pysgod");
        sighting.setLocationName("360 Hide");
        sighting.setSightedAt(LocalDateTime.of(2026, 11, 15, 10, 0));
        when(wildlifeSightingService.findLatestPublic()).thenReturn(List.of(sighting));

        mockMvc.perform(get("/webcams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("nest-cam"))
                .andExpect(jsonPath("$[0].streamUrl").value("https://www.youtube.com/embed/YnKZJ3SF5WM?rel=0"));

        mockMvc.perform(get("/sightings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].species").value("Osprey"));
    }
}
