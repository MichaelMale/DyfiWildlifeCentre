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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class POIControllerDeleteTests extends PointOfInterestControllerTests {
    @Test
    @DisplayName("Should be able to delete a POI by its ID")
    public void whenPerformingADeleteRequestForAPOIByItsID_APIReturnsCorrectStatusAndCannotFindPOI() throws Exception {
        this.mockMvc.perform(delete("/poi/delete/1")).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/poi/get/id/1")).andDo(print()).andExpect(status().isNotFound());
    }
}
