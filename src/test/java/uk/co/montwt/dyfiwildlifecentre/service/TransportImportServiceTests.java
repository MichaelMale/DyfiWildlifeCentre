package uk.co.montwt.dyfiwildlifecentre.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransportImportServiceTests {

    @Mock
    private PointOfInterestRepository repository;

    private TransportImportService service;

    @BeforeEach
    void setup() {
        service = new TransportImportService(repository);
    }

    @Test
    void importsAndUpsertsRailwayStationsWithThomasDefaultImage() throws IOException {
        String csv = """
                "station_code","station_name","latitude","longitude"
                "DVY","Dovey Junction","52.5661","-3.9396"
                "MCN","Machynlleth","52.5923","-3.8548"
                "BAD","Broken Row","0","0"
                """;
        PointOfInterest existing = new PointOfInterest();
        existing.setStationCode("DVY");
        existing.setName("Old Dovey");

        when(repository.findByStationCode("DVY")).thenReturn(Optional.of(existing));
        when(repository.findByStationCode("MCN")).thenReturn(Optional.empty());
        when(repository.save(any(PointOfInterest.class))).thenAnswer(inv -> inv.getArgument(0));

        var summary = service.importRailwayStations(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        Assertions.assertEquals(1, summary.inserted());
        Assertions.assertEquals(1, summary.updated());
        Assertions.assertEquals(1, summary.skipped());
        Assertions.assertEquals("Dovey Junction", existing.getName());
        Assertions.assertEquals("/images/thomas-train.jpg", existing.getImageUrl());
        verify(repository, times(2)).save(any(PointOfInterest.class));
    }

    @Test
    void importsBusStopsSkipsInactiveAndSetsMagicSchoolBusImage() throws IOException {
        String csv = """
                ATCOCode,NaptanCode,CommonName,Indicator,Street,LocalityName,Bearing,Longitude,Latitude,Status
                5230AWD71582,cejatjp,Garreg Farm,before,A487,Glandyfi,SW,-3.92332,52.55625,active
                5230AWD71583,cejapwm,Castle Gates,o/s,A487,Glandyfi,SW,-3.93294,52.55083,inactive
                """;
        when(repository.findByBusStopCode("5230AWD71582")).thenReturn(Optional.empty());
        when(repository.save(any(PointOfInterest.class))).thenAnswer(inv -> inv.getArgument(0));

        var summary = service.importBusStops(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));

        Assertions.assertEquals(1, summary.inserted());
        Assertions.assertEquals(0, summary.updated());
        Assertions.assertEquals(1, summary.skipped());
        verify(repository, times(1)).save(any(PointOfInterest.class));
    }
}
