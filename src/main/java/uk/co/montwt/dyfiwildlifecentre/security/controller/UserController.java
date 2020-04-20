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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.service.SecurityService;
import uk.co.montwt.dyfiwildlifecentre.security.service.UserService;

/**
 * UserController.java - A controller for the user management system. This
 * makes calls to other methods and services, and forms an HTTP API of a GET
 * and a POST request.
 *
 * @author Michael Male
 * @version 0.1 2020-04-20
 */
@Controller
public class UserController {
    private UserService userService;

    private SecurityService securityService;

    /**
     * Constructor for objects of type UserController.
     * @param userService   Autowired object of type UserService
     * @param securityService   Autowired object of type SecurityService
     */
    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * Manages a GET request to the user registration form. This method adds
     * an empty User object to the Model for it to be filled in in the form,
     * and loads the correct view.
     * @param model Model that is part of the payload of the view.
     * @return  String containing the reference to the view.
     */
    @GetMapping("/admin/registration_user")
    public String userRegistration(Model model) {
        model.addAttribute("userForm", new User());

        return "admin/registration_user";
    }

    /**
     * Manages a POST request in order to register a user. This method adds
     * the newly-registered user to the database, with a hashed password, and
     * automatically logs them in. It returns them to the admin panel homepage.
     * @param userForm  The filled-in User object referenced by the GET
     *                  request, and populated via the web form.
     * @return  String containing the admin panel homepage.
     */
    @PostMapping("/admin/register")
    public String register(@ModelAttribute("userForm") User userForm) {
        userService.save(userForm);
        securityService.autoLogin(userForm.getUsername(),
                userForm.getPassword());
        return "admin/home";
    }
}
