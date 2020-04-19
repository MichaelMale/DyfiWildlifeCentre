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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterestRepository;

import java.util.Optional;

/**
 * AdminController.java - This class contains the methods required to perform
 * the successful GET requests for the admin panel section of the web
 * application. Some of these involve fetching attributes using Thymeleaf.
 *
 * @author Michael Male
 * @version 0.1 2020-12-04
 */
@Controller
public class AdminController {

    private final PointOfInterestRepository repository;

    /**
     * Constructor for objects of class AdminController.
     * @param repository    The PointOfInterestRepository, an interface used
     *                      to connect to the SQL server.
     */
    AdminController(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets the view for adding a point of interest to the map.
     * @param model Passes a PointOfInterest constructor the view, that is
     *              populated in the form.
     * @return  String containing the correct URI for the view.
     */
    @GetMapping("/admin/add")
    public String adminAdd(Model model) {
        model.addAttribute("pointOfInterest", new PointOfInterest());
        return "admin/add";
    }

    /**
     * Gets the view for listing every Point of Interest in the database.
     * @param model Passes a List of type PointOfInterest to the view, that
     *              is used within the view to populate the list.
     * @return  String containing the correct URI for the view.
     */
    @GetMapping("/admin/list")
    public String adminList(Model model) {
        model.addAttribute("pointsOfInterest", repository.findAll());
        return "admin/list";
    }

    /**
     * Gets the view for editing a Point Of Interest.
     * @param id    Primitive of type long that contains the ID corresponding
     *             to the Point Of Interest that is to be edited.
     * @param model Finds the POI, if the ID given is valid, and passes it to
     *             the view.
     * @return  String containing the view, along with the parameter for the
     * correct Point Of Interest.
     */
    @GetMapping("/admin/edit_active")
    public String adminEditActive(@RequestParam("id") long id, Model model) {
        model.addAttribute("id", id);
        var poi = repository.findById(id);
        poi.ifPresent(pointOfInterest -> model.addAttribute("poi", pointOfInterest));
        return "admin/edit_active";
    }

    @GetMapping("/admin/users")
    public String users() {
        return "admin/users";
    }

}
