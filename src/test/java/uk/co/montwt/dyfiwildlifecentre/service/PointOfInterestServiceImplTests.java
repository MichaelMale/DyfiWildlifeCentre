package uk.co.montwt.dyfiwildlifecentre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.co.montwt.dyfiwildlifecentre.exception.PointOfInterestNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointOfInterestServiceImplTests {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private PointOfInterestRepository repository;

    @InjectMocks
    private PointOfInterestServiceImpl service;

    @AfterEach
    void tearDown() {
        PostcodeService.resetApiClient();
    }

    @Test
    void findByIdReturnsPoiWhenPresent() {
        PointOfInterest poi = new PointOfInterest("Hide", "Desc", 52.57, -3.91, "");
        poi.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(poi));

        PointOfInterest result = service.findById(1L);
        Assertions.assertEquals(poi, result);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        PointOfInterestNotFoundException ex = Assertions.assertThrows(
                PointOfInterestNotFoundException.class,
                () -> service.findById(99L));
        Assertions.assertEquals("Could not find POI 99", ex.getMessage());
    }

    @Test
    void findAllSortsByNameAscending() {
        List<PointOfInterest> expected = List.of(new PointOfInterest("A", "d", 52.5, -3.9, ""));
        when(repository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(expected);

        Assertions.assertEquals(expected, service.findAll());
    }

    @Test
    void findAllPointsOfInterestByNameDelegatesToRepository() {
        List<PointOfInterest> expected = List.of(new PointOfInterest("Hide", "d", 52.5, -3.9, ""));
        when(repository.findAllPointsOfInterestByName("Hide")).thenReturn(expected);

        Assertions.assertEquals(expected, service.findAllPointsOfInterestByName("Hide"));
    }

    @Test
    void saveWithCoordinatesComputesDistanceAndSaves() throws IOException {
        PointOfInterest poi = new PointOfInterest("Dyfi Centre", "Centre", 52.568774, -3.918031, "");

        service.save(poi);

        Assertions.assertEquals(0.0, poi.getDistanceFromCentre());
        verify(repository).save(poi);
    }

    @Test
    void saveWithNullPostcodeAndCoordinatesDoesNotThrow() throws IOException {
        PointOfInterest poi = new PointOfInterest("Test", "Desc", 52.5906, -3.8511, null);

        service.save(poi);

        Assertions.assertTrue(poi.getDistanceFromCentre() > 0);
        verify(repository).save(poi);
    }

    @Test
    void saveWithPostcodeAndZeroCoordinatesGeocodesAndSaves() throws IOException {
        PostcodeService.setApiClient(suffix -> {
            if (suffix.endsWith("/validate")) {
                return MAPPER.readTree("true");
            }
            return MAPPER.readTree("{\"latitude\":52.5906,\"longitude\":-3.8511}");
        });

        PointOfInterest poi = new PointOfInterest("Machynlleth", "Town", 0, 0, "SY20 8SR");
        service.save(poi);

        Assertions.assertEquals(52.5906, poi.getLatitude());
        Assertions.assertEquals(-3.8511, poi.getLongitude());
        Assertions.assertTrue(poi.getDistanceFromCentre() > 0);
        verify(repository).save(poi);
    }

    @Test
    void saveWithBothCoordinatesAndPostcodeThrowsPostcodeException() {
        PointOfInterest poi = new PointOfInterest("Both", "Desc", 52.5, -3.9, "SY20 8SR");

        PostcodeException ex = Assertions.assertThrows(PostcodeException.class, () -> service.save(poi));
        Assertions.assertTrue(ex.getMessage().contains("Both postcode and coordinates were entered."));
        verify(repository, never()).save(any());
    }

    @Test
    void updateRecalculatesDistanceAndSaves() {
        PointOfInterest poi = new PointOfInterest("Updated", "Desc", 0, 0, "");
        service.update(poi);

        Assertions.assertEquals(3639.0, poi.getDistanceFromCentre());
        verify(repository).save(poi);
    }

    @Test
    void deleteDelegatesToRepository() {
        service.delete(5L);
        verify(repository).deleteById(5L);
    }
}
