/*
 * Copyright (C) 2020 Michael Male
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package uk.co.montwt.dyfiwildlifecentre;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    public void uponLoadingPage_contentAppears() {
        final String pageAsText = page.asText();
        Assertions.assertFalse(pageAsText.isEmpty());
    }

    @Test
    @DisplayName("When the index page is opened, a navbar should appear with the correct text.")
    public void uponLoadingPage_navBarElementAppears() {
        DomElement menu = page.getElementById("primary-navbar");
        List<String> expectedLines = List.of("Dyfi Wildlife Centre", "Admin Panel");
        List<String> actualLines = Arrays.asList(menu.getVisibleText().split("\\r?\\n"));
        Assertions.assertLinesMatch(expectedLines, actualLines);
    }

    @Test
    @DisplayName("When the index page is opened, the navbar should be light blue.")
    public void uponLoadingPage_navBarIsLightBlue() {
        DomElement menu = page.getElementById("primary-navbar");
        Assertions.assertEquals("light-blue", menu.getAttribute("class"));
    }

    @Test
    @DisplayName("When the index page is opened, the map element should appear.")
    public void uponLoadingPage_mapElementAppears() {
        DomElement map = page.getElementById("map");
        Assertions.assertNotNull(map);
    }

    @Test
    @DisplayName("When the index page is opened, the map should be in a column class.")
    public void uponLoadingPage_mapShowsColS12M6() {
        DomElement map = page.getElementById("map");
        Assertions.assertEquals("col s12 m6", map.getAttribute("class"));
    }

}
