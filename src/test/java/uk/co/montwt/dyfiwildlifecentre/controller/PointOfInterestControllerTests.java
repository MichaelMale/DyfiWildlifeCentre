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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PointOfInterestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointOfInterestController controller;

    @Test
    @DisplayName("Ensure that the context is creating the controller")
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("Should get the correct POI labelled '1'")
    public void whenPerformingGetRequest_ControllerShouldGetCorrectPOI() throws Exception {
        String expectedString = "{\"id\":1,\"name\":\"Aberystwyth University\",\"description\":\"Aberystwyth University" +
                " (Welsh: Prifysgol Aberystwyth) is a public research university in Aberystwyth, Wales. Aberystwyth was" +
                " a founding member institution of the former federal University of Wales. The university has over 8,000" +
                " students studying across 3 academic faculties and 17 departments.\",\"latitude\":52.41806,\"longitude\"" +
                ":-4.06576}\n";

        this.mockMvc.perform(get("/poi/get/id/1")).andDo(print()).andExpect(content().string(expectedString));
    }

}

