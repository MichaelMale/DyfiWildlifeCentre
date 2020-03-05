/*
 * Copyright (C) 2020 Michael Male
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package uk.co.montwt.dyfiwildlifecentre.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;

import java.net.URI;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class POIControllerPostTests extends PointOfInterestControllerTests{

    @Test
    @DisplayName("Should be able to add a POI to the database")
    public void confirmThatDatabaseAllowsYouToAddAPointOfInterest() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        TestRestTemplate testRestTemplate = new TestRestTemplate();

        PointOfInterest newPOI = new PointOfInterest("Test1", "Test1Description", 0, 0);
        final String baseUrl = "http://localhost:8080/poi/create";
        URI uri = new URI(baseUrl);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/poi/create")
                        .content(newPOI.toJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

}
