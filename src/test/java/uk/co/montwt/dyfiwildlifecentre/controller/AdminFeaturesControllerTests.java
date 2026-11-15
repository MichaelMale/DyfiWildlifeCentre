package uk.co.montwt.dyfiwildlifecentre.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.montwt.dyfiwildlifecentre.model.WebcamFeed;
import uk.co.montwt.dyfiwildlifecentre.model.WildlifeSighting;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;
import uk.co.montwt.dyfiwildlifecentre.security.WebSecurityConfig;
import uk.co.montwt.dyfiwildlifecentre.service.TransportImportService;
import uk.co.montwt.dyfiwildlifecentre.service.WebcamService;
import uk.co.montwt.dyfiwildlifecentre.service.WildlifeSightingService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = {AdminWebcamController.class, AdminSightingController.class, AdminImportController.class})
@Import(WebSecurityConfig.class)
public class AdminFeaturesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebcamService webcamService;

    @MockitoBean
    private WildlifeSightingService wildlifeSightingService;

    @MockitoBean
    private TransportImportService transportImportService;

    @MockitoBean
    private PointOfInterestRepository pointOfInterestRepository;

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminIsForbiddenFromAdminFeaturePages() throws Exception {
        mockMvc.perform(get("/admin/webcams")).andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/sightings")).andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/import")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void adminCanManageWebcamsSightingsAndCsvImport() throws Exception {
        WebcamFeed savedCam = new WebcamFeed();
        savedCam.setNameEn("Estuary Cam");
        when(webcamService.findAll()).thenReturn(List.of(savedCam));
        when(webcamService.saveOrUpdate(any(WebcamFeed.class))).thenReturn(savedCam);

        mockMvc.perform(get("/admin/webcams"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/webcams"));

        mockMvc.perform(post("/admin/webcams/save")
                        .with(csrf())
                        .param("nameEn", "Estuary Cam")
                        .param("streamUrl", "https://www.youtube.com/watch?v=YnKZJ3SF5WM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/webcams"))
                .andExpect(flash().attribute("flashSuccessKey", "admin.flash.webcamSaved"));

        WildlifeSighting savedSighting = new WildlifeSighting();
        savedSighting.setSpecies("Red Kite");
        when(wildlifeSightingService.findAll()).thenReturn(List.of(savedSighting));
        when(wildlifeSightingService.record(any(WildlifeSighting.class), eq("dyfiadmin"))).thenReturn(savedSighting);

        mockMvc.perform(get("/admin/sightings"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sightings"));

        mockMvc.perform(post("/admin/sightings/create")
                        .with(csrf())
                        .param("species", "Red Kite")
                        .param("locationName", "Boardwalk"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sightings"))
                .andExpect(flash().attribute("flashSuccessKey", "admin.flash.sightingCreated"));

        when(pointOfInterestRepository.countByStationCodeIsNotNull()).thenReturn(224L);
        when(pointOfInterestRepository.countByBusStopCodeIsNotNull()).thenReturn(247L);
        when(transportImportService.importRailwayStations(any()))
                .thenReturn(new TransportImportService.ImportSummary("stations", 2, 1, 0));

        mockMvc.perform(get("/admin/import"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/import"));

        MockMultipartFile csvFile = new MockMultipartFile(
                "file", "stations.csv", "text/csv",
                "station_code,station_name,latitude,longitude\n".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/admin/import/stations").file(csvFile).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/import"))
                .andExpect(flash().attribute("flashSuccessKey", "admin.flash.transportSynced"));

        verify(wildlifeSightingService).record(any(WildlifeSighting.class), eq("dyfiadmin"));
    }
}
