package uk.co.montwt.dyfiwildlifecentre;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.transaction.annotation.Transactional;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;
import uk.co.montwt.dyfiwildlifecentre.security.model.Role;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.RoleRepository;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.UserRepository;
import uk.co.montwt.dyfiwildlifecentre.security.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DyfiWildlifeCentreIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private WebClient webClient;

    @BeforeEach
    void setup() {
        webClient = MockMvcWebClientBuilder.mockMvcSetup(mockMvc).build();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        User admin = new User();
        admin.setUsername("adminuser");
        admin.setPassword("password123");
        userService.save(admin);
    }

    @Test
    void contextLoadsAndRepositoriesWorkAgainstH2() {
        PointOfInterest poi = new PointOfInterest("Dyfi Osprey Project", "Reserve", 52.568774, -3.918031, "");
        poi.setCategory("wildlife");
        poi.setNameCy("Prosiect Gwalch y Pysgod Dyfi");
        poi.setDescriptionCy("Gwarchodfa natur");
        pointOfInterestRepository.save(poi);

        List<PointOfInterest> found = pointOfInterestRepository.findAllPointsOfInterestByName("dyfi osprey project");
        Assertions.assertEquals(1, found.size());
        Assertions.assertEquals("Dyfi Osprey Project", found.get(0).getName());
        Assertions.assertEquals("Prosiect Gwalch y Pysgod Dyfi", found.get(0).getNameCy());
        Assertions.assertEquals("Gwarchodfa natur", found.get(0).getDescriptionCy());

        User savedUser = userRepository.findByUsername("adminuser");
        Assertions.assertNotNull(savedUser);
        Assertions.assertNotEquals("password123", savedUser.getPassword());
    }

    @Test
    void formLoginAuthenticatesAgainstDatabaseAndAccessesAdminPanel() throws Exception {
        mockMvc.perform(formLogin("/login").user("adminuser").password("password123"))
                .andExpect(authenticated().withUsername("adminuser").withRoles("ADMIN"))
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/poi"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    void htmlUnitRendersIndexAndLogsIntoAdminPanel() throws Exception {
        HtmlPage indexPage = webClient.getPage("http://localhost/");
        Assertions.assertEquals("Dyfi Wildlife Centre", indexPage.getTitleText());
        String indexXml = indexPage.asXml();
        Assertions.assertTrue(indexXml.contains("leaflet.js"));
        Assertions.assertTrue(indexXml.contains("leaflet.markercluster.js"));
        Assertions.assertTrue(indexXml.contains("/js/osm-map.js"));
        Assertions.assertFalse(indexXml.contains("maps.googleapis.com"));
        Assertions.assertFalse(indexXml.contains("googlemaps.js"));

        HtmlPage loginPage = webClient.getPage("http://localhost/login");
        HtmlForm form = loginPage.getForms().stream()
                .filter(f -> f.getActionAttribute().endsWith("/login"))
                .findFirst()
                .orElseThrow();
        form.getInputByName("username").type("adminuser");
        form.getInputByName("password").type("password123");
        form.getInputByValue("Sign In").click();

        HtmlPage adminPage = webClient.getPage("http://localhost/admin");
        Assertions.assertEquals("Dyfi Wildlife Centre - Admin Panel", adminPage.getTitleText());
        Assertions.assertTrue(adminPage.asNormalizedText().contains("Hello, adminuser!"));

        HtmlPage welshIndexPage = webClient.getPage("http://localhost/?lang=cy");
        Assertions.assertEquals("Canolfan Bywyd Gwyllt Dyfi", welshIndexPage.getTitleText());
        Assertions.assertTrue(welshIndexPage.asNormalizedText().contains("Panel Gweinyddu"));
    }
}
