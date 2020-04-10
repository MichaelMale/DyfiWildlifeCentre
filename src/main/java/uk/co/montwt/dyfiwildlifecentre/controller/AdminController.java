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

@Controller
public class AdminController {

    private final PointOfInterestRepository repository;

    AdminController(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/admin/add")
    public String adminAdd(Model model) {
        model.addAttribute("pointOfInterest", new PointOfInterest());
        return "admin/add";
    }

    @GetMapping("/admin/list")
    public String adminList(Model model) {
        model.addAttribute("pointsOfInterest", repository.findAll());
        return "admin/list";
    }

    @GetMapping("/admin/edit_active")
    public String adminEditActive(@RequestParam("id") long id, Model model) {
        model.addAttribute("id", id);
        var poi = repository.findById(id);
        poi.ifPresent(pointOfInterest -> model.addAttribute("poi", pointOfInterest));
        return "admin/edit_active";
    }


}
