package uk.co.montwt.dyfiwildlifecentre.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.security.WebSecurityConfig;
import uk.co.montwt.dyfiwildlifecentre.security.controller.UserController;
import uk.co.montwt.dyfiwildlifecentre.security.controller.UserRestController;
import uk.co.montwt.dyfiwildlifecentre.security.model.Role;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.service.SecurityService;
import uk.co.montwt.dyfiwildlifecentre.security.service.UserService;
import uk.co.montwt.dyfiwildlifecentre.service.PointOfInterestServiceImpl;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AdminController.class, UserController.class, UserRestController.class})
@Import({WebSecurityConfig.class, MvcConfig.class})
public class AdminAndSecurityControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointOfInterestServiceImpl pointOfInterestService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityService securityService;

    @Test
    void publicPagesRenderWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("directionsModal")))
                .andExpect(content().string(containsString("webcamModal")))
                .andExpect(content().string(containsString("webcamYoutubePlayer")))
                .andExpect(content().string(containsString("webcamVideoPlayer")));
        mockMvc.perform(get("/index")).andExpect(status().isOk()).andExpect(view().name("index"));
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
    }

    @Test
    void localeCanSwitchBetweenWelshAndEnglishViaLangParamAndCookie() throws Exception {
        mockMvc.perform(get("/").param("lang", "cy"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("dyfi_lang", "cy"))
                .andExpect(content().string(containsString("lang=\"cy\"")))
                .andExpect(content().string(containsString("Canolfan Bywyd Gwyllt Dyfi")))
                .andExpect(content().string(containsString("Panel Gweinyddu")));

        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("dyfi_lang", "en"))
                .andExpect(content().string(containsString("lang=\"en\"")))
                .andExpect(content().string(containsString("Dyfi Wildlife Centre")))
                .andExpect(content().string(containsString("Admin Panel")));
    }

    @Test
    void adminPagesRedirectUnauthenticatedRequestsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mockMvc.perform(get("/admin/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "visitor", roles = "USER")
    void nonAdminUserGetsForbiddenOnAdminRouteAnd403TemplateRenders() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/403"));

        mockMvc.perform(get("/403"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/403"))
                .andExpect(content().string(containsString("visitor")));
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void adminHomeAndNavbarRenderUsernameWithThymeleafSecurityDialect() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(content().string(containsString("dyfiadmin")));

        mockMvc.perform(get("/admin").param("lang", "cy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Panel Gweinyddu")))
                .andExpect(content().string(containsString("Ychwanegu")))
                .andExpect(content().string(containsString("Helo,")));
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void adminAddListAndEditPagesRenderWithModelAttributes() throws Exception {
        PointOfInterest poi = new PointOfInterest("360 Hide", "Viewpoint", 52.568, -3.917, "");
        poi.setId(4L);
        poi.setNameCy("Cuddfan 360");
        poi.setDescriptionCy("Golygfan");
        when(pointOfInterestService.findAll()).thenReturn(List.of(poi));
        when(pointOfInterestService.findById(4L)).thenReturn(poi);

        mockMvc.perform(get("/admin/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/add"))
                .andExpect(model().attributeExists("pointOfInterest"))
                .andExpect(content().string(containsString("nameCy")))
                .andExpect(content().string(containsString("descriptionCy")))
                .andExpect(content().string(containsString("imageUrl")))
                .andExpect(content().string(containsString("imageFile")))
                .andExpect(content().string(containsString("addPoiPickerMap")))
                .andExpect(content().string(containsString("/js/poi-map-picker.js")));

        mockMvc.perform(get("/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/list"))
                .andExpect(content().string(containsString("360 Hide")))
                .andExpect(content().string(containsString("Cuddfan 360")));

        mockMvc.perform(get("/admin/edit_active").param("id", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit_active"))
                .andExpect(model().attribute("id", 4L))
                .andExpect(content().string(containsString("360 Hide")))
                .andExpect(content().string(containsString("Cuddfan 360")))
                .andExpect(content().string(containsString("Golygfan")));
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void adminListSeparatesRailwayStationsAndBusStopsBehindConfirmation() throws Exception {
        PointOfInterest general = new PointOfInterest("360 Hide", "Viewpoint", 52.568, -3.917, "");
        general.setId(4L);

        PointOfInterest station = new PointOfInterest("Dovey Junction", "National Rail station (DVY)", 52.5643723647, -3.9239110192, "");
        station.setId(101L);
        station.setCategory("transport");
        station.setStationCode("DVY");

        PointOfInterest busStop = new PointOfInterest("Garreg Farm, Glandyfi (SW)", "Bus stop (5230AWD71582)", 52.556247176, -3.923320283, "");
        busStop.setId(201L);
        busStop.setCategory("transport");
        busStop.setBusStopCode("5230AWD71582");

        when(pointOfInterestService.findAll()).thenReturn(List.of(general, station, busStop));
        when(pointOfInterestService.findById(101L)).thenReturn(station);
        when(pointOfInterestService.findById(201L)).thenReturn(busStop);

        mockMvc.perform(get("/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/list"))
                .andExpect(model().attribute("generalPois", List.of(general)))
                .andExpect(model().attribute("railwayStations", List.of(station)))
                .andExpect(model().attribute("busStops", List.of(busStop)))
                .andExpect(content().string(containsString("Railway Stations")))
                .andExpect(content().string(containsString("Bus Stops")))
                .andExpect(content().string(containsString("stationSearch")))
                .andExpect(content().string(containsString("busStopSearch")))
                .andExpect(content().string(containsString("stationConfirmModal")))
                .andExpect(content().string(containsString("DVY")))
                .andExpect(content().string(containsString("5230AWD71582")))
                .andExpect(content().string(containsString("Dovey Junction")))
                .andExpect(content().string(containsString("Garreg Farm, Glandyfi (SW)")));

        mockMvc.perform(get("/admin/edit_active").param("id", "101"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit_active"))
                .andExpect(content().string(containsString("Protected Railway Station (DVY)")))
                .andExpect(content().string(containsString("unlockStationBtn")))
                .andExpect(content().string(containsString("unlockStationModal")))
                .andExpect(content().string(containsString("data-station-locked=\"true\"")));

        mockMvc.perform(get("/admin/edit_active").param("id", "201"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit_active"))
                .andExpect(content().string(containsString("Protected Bus Stop (5230AWD71582)")))
                .andExpect(content().string(containsString("unlockStationBtn")))
                .andExpect(content().string(containsString("unlockStationModal")))
                .andExpect(content().string(containsString("data-station-locked=\"true\"")));
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void userRegistrationPassesRawPasswordToAutoLogin() throws Exception {
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setPassword("$2a$10$encodedHash");
            return null;
        }).when(userService).save(any(User.class));

        mockMvc.perform(post("/admin/register")
                        .with(csrf())
                        .param("username", "newadmin")
                        .param("password", "rawSecret99"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"));

        verify(securityService).autoLogin("newadmin", "rawSecret99");
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void duplicateUsernameRegistrationRenders409ErrorPage() throws Exception {
        doThrow(new DataIntegrityViolationException("duplicate username"))
                .when(userService).save(any(User.class));

        mockMvc.perform(post("/admin/register")
                        .with(csrf())
                        .param("username", "existinguser")
                        .param("password", "secret1234"))
                .andExpect(status().isConflict())
                .andExpect(view().name("error/409"));
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void usersRestEndpointOmitsPasswordsAndRoles() throws Exception {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        User user = new User();
        user.setId(1L);
        user.setUsername("dyfiadmin");
        user.setPassword("secretHash");
        user.setRoles(Set.of(role));
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("dyfiadmin")))
                .andExpect(content().string(not(containsString("secretHash"))));
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void adminUsersPageAndDeleteUserWork() throws Exception {
        User user = new User();
        user.setId(3L);
        user.setUsername("staffmember");
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(content().string(containsString("staffmember")));

        mockMvc.perform(post("/users/delete").with(csrf()).param("id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("flashSuccessKey", "admin.flash.userDeleted"));

        verify(userService).delete(3L);
    }

    @Test
    @WithMockUser(username = "dyfiadmin", roles = "ADMIN")
    void adminCannotDeleteSelfAndCurrentAccountBadgeShown() throws Exception {
        User currentAdmin = new User();
        currentAdmin.setId(1L);
        currentAdmin.setUsername("dyfiadmin");
        User otherStaff = new User();
        otherStaff.setId(2L);
        otherStaff.setUsername("staffmember");
        when(userService.getAll()).thenReturn(List.of(currentAdmin, otherStaff));
        when(userService.findById(1L)).thenReturn(currentAdmin);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Current account")))
                .andExpect(content().string(containsString("staffmember")));

        mockMvc.perform(post("/users/delete").with(csrf()).param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("flashErrorKey", "admin.flash.cannotDeleteSelf"));

        verify(userService, never()).delete(1L);
    }

    @Test
    @WithMockUser(username = "viewer", roles = "USER")
    void nonAdminCannotAccessUserManagementEndpoints() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/users/delete").with(csrf()).param("id", "1"))
                .andExpect(status().isForbidden());

        verify(userService, never()).delete(anyLong());
    }
}
