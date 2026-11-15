package uk.co.montwt.dyfiwildlifecentre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DirectionsServiceTests {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private PointOfInterestRepository repository;

    private DirectionsService directionsService;

    @BeforeEach
    void setup() {
        directionsService = new DirectionsService(repository);
    }

    @Test
    void drivingDirectionsResolveTownAndParseOsrmSteps() {
        when(repository.findAll()).thenReturn(List.of());
        directionsService.setHttpClient(url -> {
            if (url.contains("nominatim.openstreetmap.org")) {
                return MAPPER.readTree("""
                        [{"lat":"52.4153","lon":"-4.0829","display_name":"Aberystwyth, Ceredigion, Wales"}]
                        """);
            }
            return MAPPER.readTree("""
                    {
                      "code":"Ok",
                      "routes":[{
                        "distance": 28968.2,
                        "duration": 2100.0,
                        "geometry": {"coordinates": [[-3.918031, 52.568774], [-4.0829, 52.4153]]},
                        "legs": [{
                          "steps": [
                            {"distance": 1609.344, "name": "A487", "maneuver": {"type": "depart", "modifier": "south"}},
                            {"distance": 0, "name": "", "maneuver": {"type": "arrive"}}
                          ]
                        }]
                      }]
                    }
                    """);
        });

        DirectionsService.DirectionsResult result = directionsService.getDirections("Aberystwyth", "driving");

        Assertions.assertEquals("driving", result.mode());
        Assertions.assertEquals("Aberystwyth, Ceredigion, Wales", result.destinationLabel());
        Assertions.assertEquals(18.0, result.distanceMiles());
        Assertions.assertEquals(35L, result.durationMinutes());
        Assertions.assertFalse(result.estimated());
        Assertions.assertEquals(2, result.coordinates().size());
        Assertions.assertEquals(52.568774, result.coordinates().get(0)[0]);
        Assertions.assertEquals(-3.918031, result.coordinates().get(0)[1]);
        Assertions.assertTrue(result.steps().get(0).contains("A487"));
    }

    @Test
    void transitDirectionsReturnNearestTransportHubsOrderedByDistance() {
        PointOfInterest dovey = new PointOfInterest("Dovey Junction", "Rail", 52.5661, -3.9396, "");
        dovey.setId(10L);
        dovey.setCategory("transport");
        dovey.setNameCy("Cyffordd Dyfi");

        PointOfInterest mach = new PointOfInterest("Machynlleth Station", "Rail", 52.5923, -3.8548, "");
        mach.setId(11L);
        mach.setCategory("transport");

        PointOfInterest cafe = new PointOfInterest("Cafe", "Food", 52.58, -3.86, "");
        cafe.setId(12L);
        cafe.setCategory("business");

        when(repository.findAll()).thenReturn(List.of(mach, cafe, dovey));
        directionsService.setHttpClient(url -> MAPPER.readTree("""
                [{"lat":"52.5906","lon":"-3.8511","display_name":"Machynlleth, Powys, Wales"}]
                """));

        DirectionsService.DirectionsResult result = directionsService.getDirections("Machynlleth", "transit");

        Assertions.assertEquals("transit", result.mode());
        Assertions.assertEquals(2, result.nearestTransitHubs().size());
        Assertions.assertEquals("Dovey Junction", result.nearestTransitHubs().get(0).name());
        Assertions.assertEquals("Cyffordd Dyfi", result.nearestTransitHubs().get(0).nameCy());
        Assertions.assertEquals("Machynlleth Station", result.nearestTransitHubs().get(1).name());
        Assertions.assertTrue(result.steps().get(0).contains("Dovey Junction"));
        Assertions.assertEquals("https://www.traveline.cymru/", result.transitPlannerUrl());
    }

    @Test
    void blankOrUnknownDestinationThrowsIllegalArgument() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> directionsService.getDirections("   ", "driving"));

        directionsService.setHttpClient(url -> MAPPER.readTree("[]"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> directionsService.getDirections("NowhereThatExistsXYZ", "driving"));
    }

    @Test
    void coordinatePairQueryResolvesDirectlyWithoutExternalGeocoding() {
        directionsService.setHttpClient(url -> {
            if (url.contains("router.project-osrm.org")) {
                return MAPPER.readTree("""
                        {
                          "code": "Ok",
                          "routes": [{
                            "distance": 1609.344,
                            "duration": 180,
                            "geometry": { "coordinates": [[-3.918031, 52.568774], [-3.923911, 52.564372]] },
                            "legs": [{ "steps": [] }]
                          }]
                        }
                        """);
            }
            throw new AssertionError("Nominatim should not be called for coordinates: " + url);
        });

        DirectionsService.DirectionsResult result = directionsService.getDirections("52.564372, -3.923911", "driving");
        Assertions.assertEquals("52.56437, -3.92391", result.destinationLabel());
        Assertions.assertEquals(1.0, result.distanceMiles());
        Assertions.assertEquals(3L, result.durationMinutes());
    }

    @Test
    void toCentreDirectionRoutesFromOriginToWildlifeCentre() {
        when(repository.findAll()).thenReturn(List.of());
        directionsService.setHttpClient(url -> {
            if (url.contains("router.project-osrm.org")) {
                Assertions.assertTrue(url.contains("-4.082900,52.415300;-3.918031,52.568774"), url);
                return MAPPER.readTree("""
                        {
                          "code": "Ok",
                          "routes": [{
                            "distance": 28968.2,
                            "duration": 2100,
                            "geometry": { "coordinates": [[-4.0829, 52.4153], [-3.918031, 52.568774]] },
                            "legs": [{ "steps": [] }]
                          }]
                        }
                        """);
            }
            return MAPPER.readTree("[]");
        });

        DirectionsService.DirectionsResult result = directionsService.getDirections("52.4153, -4.0829", "driving", "toCentre");
        Assertions.assertEquals("toCentre", result.direction());
        Assertions.assertEquals("52.41530, -4.08290", result.originLabel());
        Assertions.assertEquals("Dyfi Wildlife Centre", result.destinationLabel());
        Assertions.assertEquals(52.4153, result.remoteLat(), 0.0001);
        Assertions.assertEquals(-4.0829, result.remoteLng(), 0.0001);
    }
}
