package uk.co.montwt.dyfiwildlifecentre;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.assertj.core.api.Assert;
import org.assertj.core.error.AssertionErrorMessagesAggregrator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@SpringBootTest
@WebAppConfiguration
public class IndexViewTests {

    @Autowired
    private WebApplicationContext wac;

    private HtmlPage page;
    private WebClient webClient;

    @BeforeEach
    public void setup() throws IOException {
        webClient = MockMvcWebClientBuilder.webAppContextSetup(wac).build();
        page = webClient.getPage("http://localhost:8080/");
    }

    @AfterEach
    public void finish() {
        webClient.close();
        page.cleanUp();
    }

    @Test
    @DisplayName("When the index page is opened, content appears on the page.")
    public void uponLoadingPage_contentAppears()  {
        final String pageAsText = page.asText();
        Assertions.assertFalse(pageAsText.isEmpty());
    }

    @Test
    @DisplayName("When the index page is opened, the map element should appear.")
    public void uponLoadingPage_mapElementAppears() {
        DomElement map = page.getElementById("map");
        Assertions.assertNotNull(map);
    }

}
