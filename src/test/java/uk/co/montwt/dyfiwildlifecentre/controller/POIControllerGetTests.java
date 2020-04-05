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
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class POIControllerGetTests extends PointOfInterestControllerTests {
    @Test
    @DisplayName("Should get the correct POI labelled '1'")
    public void whenPerformingGetRequestOnId_ControllerShouldGetCorrectPOI() throws Exception {
        String expectedString =
                "{\"id\":1,\"name\":\"Dyfi Wildlife Centre\"," +
                        "\"description\":\"This is the amazing place where things happen.\",\"latitude\":52.568774,\"longitude\":-3.918031}";
        this.mockMvc.perform(get("/poi/get/id/1")).andDo(print()).andExpect(status().isFound()).andExpect(content().string(expectedString));
    }

    @Test
    @DisplayName("When an ID that is not in the database is requested an HTTP 404 status code should be returned")
    public void whenIDThatIsNotInTheDatabaseIsRequested_HTTP404StatusCodeShouldBeReturned() throws Exception {
        this.mockMvc.perform(get("/poi/get/id/-1")).andDo(print()).andExpect(status().isNotFound());
    }

}
