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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.service.SecurityService;
import uk.co.montwt.dyfiwildlifecentre.security.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * Manages a GET request to return a list of all users..
     *
     * @param model Part of view payload
     * @return  User list view
     */
    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAll());
        return "admin/users";
    }

    /**
     * Manages a request to delete a user from the database.
     *
     * @param id    Long containing the ID of the User to be deleted
     * @return  A redirect to the user list
     */
    @GetMapping("/users/delete")
    public RedirectView deleteUserByUsername(@RequestParam("id") Long id) {
        userService.delete(id);
        return new RedirectView("/admin/users");
    }

    /**
     * Manages a data integrity violation, by returning a page with details
     * of the exception that has occurred.
     *
     * @param req   Contains the HTTP request
     * @param ex    Contains the exception
     * @return  The error view, along with the members of the method.
     */
    @ResponseStatus(value=HttpStatus.CONFLICT,reason="Data Integrity Violation")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView conflict(HttpServletRequest req, Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error/409");
        return mav;
    }
}
