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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MasterControllerTests {

    @Autowired
    private MasterController controller;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("Confirm that the index method returns the correct string")
    void onCallingIndexMethod_ConfirmMethodReturnsCorrectString() {
        Assertions.assertEquals("index", controller.index());
    }

//    @Test
//    @DisplayName("Confirm that the admin method returns the correct string")
//    void onCallingAdminMethod_ConfirmMethodReturnsCorrectString() {
//        Assertions.assertEquals("admin", controller.admin());
//    }
}
