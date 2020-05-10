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

package uk.co.montwt.dyfiwildlifecentre.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.service.UserService;

import java.util.List;

/**
 * UserResourceController.java - This contains methods to perform RESTful API
 * calls on the User entity.
 *
 * @author Michael Male
 * @version 0.2 2020-04-24
 */
@RestController
public class UserRestController {

    private UserService userService;

    /**
     * Constructor for objects of type UserResourceController.
     * @param userService   An autowired user service.
     */
    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Manages a GET request to return all users in the database.
     * @return  list of type User
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAll();
    }
}
