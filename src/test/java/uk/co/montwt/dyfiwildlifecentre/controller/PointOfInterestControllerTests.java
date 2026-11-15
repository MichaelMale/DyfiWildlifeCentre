package uk.co.montwt.dyfiwildlifecentre.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.montwt.dyfiwildlifecentre.exception.PointOfInterestNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.security.WebSecurityConfig;
import uk.co.montwt.dyfiwildlifecentre.service.ImageStorageService;
import uk.co.montwt.dyfiwildlifecentre.service.PointOfInterestServiceImpl;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointOfInterestController.class)
@Import(WebSecurityConfig.class)
public class PointOfInterestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointOfInterestServiceImpl pointOfInterestService;

    @MockitoBean
    private ImageStorageService imageStorageService;

    @Test
    void getAllPointsOfInterestIsPublicAndReturnsJson() throws Exception {
        PointOfInterest poi = new PointOfInterest("Osprey Hide", "Viewing hide", 52.5687, -3.918, "");
        poi.setId(1L);
        when(pointOfInterestService.findAll()).thenReturn(List.of(poi));

        mockMvc.perform(get("/poi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Osprey Hide"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPointOfInterestByIdReturnsPoiWhenFound() throws Exception {
        PointOfInterest poi = new PointOfInterest("Visitor Centre", "Main building", 52.5687, -3.918, "");
        poi.setId(2L);
        when(pointOfInterestService.findById(2L)).thenReturn(poi);

        mockMvc.perform(get("/poi/get/id/2"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.name").value("Visitor Centre"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPointOfInterestByIdReturns404ViaAdviceWhenMissing() throws Exception {
        when(pointOfInterestService.findById(404L)).thenThrow(new PointOfInterestNotFoundException(404L));

        mockMvc.perform(get("/poi/get/id/404"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Could not find POI 404"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPointOfInterestsByNameReturnsMatches() throws Exception {
        PointOfInterest poi = new PointOfInterest("Hide", "North hide", 52.57, -3.91, "");
        when(pointOfInterestService.findAllPointsOfInterestByName("Hide")).thenReturn(List.of(poi));

        mockMvc.perform(get("/poi/get/name/Hide"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$[0].name").value("Hide"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitPointOfInterestSavesAndRedirectsToIndex() throws Exception {
        mockMvc.perform(post("/poi/form_create")
                        .with(csrf())
                        .param("name", "Boardwalk")
                        .param("nameCy", "Llwybr Pren")
                        .param("description", "Wooden walkway")
                        .param("descriptionCy", "Llwybr cerdded pren")
                        .param("latitude", "52.569")
                        .param("longitude", "-3.917")
                        .param("postcode", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));

        verify(pointOfInterestService).save(argThat(poi ->
                "Boardwalk".equals(poi.getName())
                        && "Llwybr Pren".equals(poi.getNameCy())
                        && "Llwybr cerdded pren".equals(poi.getDescriptionCy())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitPointOfInterestHandlesPostcodeExceptionWith400View() throws Exception {
        doThrow(new PostcodeException("ZZ01 1AA", "Invalid postcode"))
                .when(pointOfInterestService).save(any(PointOfInterest.class));

        mockMvc.perform(post("/poi/form_create")
                        .with(csrf())
                        .param("name", "Bad")
                        .param("postcode", "ZZ01 1AA"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/400"))
                .andExpect(model().attribute("exception", containsString("Invalid postcode")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitPointOfInterestHandlesDuplicateLocationConstraint() throws Exception {
        ConstraintViolationException cve = new ConstraintViolationException(
                "duplicate", new SQLException("duplicate"), "points_of_interest_latitude_longitude_key");
        doThrow(new DataIntegrityViolationException("constraint", cve))
                .when(pointOfInterestService).save(any(PointOfInterest.class));

        mockMvc.perform(post("/poi/form_create")
                        .with(csrf())
                        .param("name", "Duplicate"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/400"))
                .andExpect(model().attribute("exception", "There is already a POI with this location."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitPointOfInterestHandlesDataIntegrityViolationWithNullConstraintName() throws Exception {
        doThrow(new DataIntegrityViolationException("generic failure", new RuntimeException("DB error")))
                .when(pointOfInterestService).save(any(PointOfInterest.class));

        mockMvc.perform(post("/poi/form_create")
                        .with(csrf())
                        .param("name", "Broken"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/400"))
                .andExpect(model().attribute("exception", "DB error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePointOfInterestSetsIdAndRedirects() throws Exception {
        mockMvc.perform(post("/poi/update")
                        .with(csrf())
                        .param("id", "12")
                        .param("name", "Updated Hide")
                        .param("nameCy", "Cuddfan Diweddarwyd")
                        .param("descriptionCy", "Disgrifiad newydd")
                        .param("latitude", "52.57")
                        .param("longitude", "-3.92"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(pointOfInterestService).update(argThat(poi ->
                poi.getId() == 12L
                        && "Updated Hide".equals(poi.getName())
                        && "Cuddfan Diweddarwyd".equals(poi.getNameCy())
                        && "Disgrifiad newydd".equals(poi.getDescriptionCy())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePointOfInterestRedirectsToAdminList() throws Exception {
        mockMvc.perform(post("/poi/delete").with(csrf()).param("id", "9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/list"));

        verify(pointOfInterestService).delete(9L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void submitPointOfInterestStoresUploadedImageFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "imageFile", "osprey.png", "image/png", new byte[]{1, 2, 3, 4});
        when(imageStorageService.storeImage(any())).thenReturn("/uploads/stored-osprey.png");

        mockMvc.perform(multipart("/poi/form_create")
                        .file(file)
                        .with(csrf())
                        .param("name", "Nest Platform")
                        .param("latitude", "52.569")
                        .param("longitude", "-3.918"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));

        verify(pointOfInterestService).save(argThat(poi ->
                "Nest Platform".equals(poi.getName())
                        && "/uploads/stored-osprey.png".equals(poi.getImageUrl())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePointOfInterestPreservesHotlinkedImageUrl() throws Exception {
        mockMvc.perform(post("/poi/update")
                        .with(csrf())
                        .param("id", "12")
                        .param("name", "Updated Hide")
                        .param("imageUrl", "https://example.org/hide.jpg")
                        .param("latitude", "52.57")
                        .param("longitude", "-3.92"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(pointOfInterestService).update(argThat(poi ->
                poi.getId() == 12L
                        && "https://example.org/hide.jpg".equals(poi.getImageUrl())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminIsForbiddenFromPoiMutationEndpoints() throws Exception {
        mockMvc.perform(get("/poi/get/id/2"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/poi/form_create")
                        .with(csrf())
                        .param("name", "Blocked")
                        .param("latitude", "52.57")
                        .param("longitude", "-3.92"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/poi/update")
                        .with(csrf())
                        .param("id", "2")
                        .param("name", "Blocked")
                        .param("latitude", "52.57")
                        .param("longitude", "-3.92"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/poi/delete").with(csrf()).param("id", "2"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pointOfInterestService);
    }
}
