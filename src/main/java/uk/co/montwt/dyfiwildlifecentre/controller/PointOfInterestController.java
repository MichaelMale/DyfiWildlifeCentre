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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.montwt.dyfiwildlifecentre.exception.PointOfInterestNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterestRepository;

import java.net.URI;
import java.util.List;

/**
 * PointOfInterestController.java - A controller that implements a RESTful
 * API for points of interest, including CRUD methods.
 *
 * @author Michael Male
 * @version 0.1 2020-04-12
 * @see RestController
 */
@RestController
public class PointOfInterestController{

    private final PointOfInterestRepository repository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor for objects of type PointOfInterestController, that uses
     * the repository for POIs.
     * @param repository    Object of type PointOfInterestRepository.
     */
    PointOfInterestController(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets a POI by its ID.
     *
     * @param id ID of the POI.
     * @return POI that relates to this ID, null if not found.
     */
    @GetMapping("/poi/get/id/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public PointOfInterest getPointOfInterestById(@PathVariable("id") long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PointOfInterestNotFoundException(id));
    }

    /**
     * Gets all Points of Interest in the DAO.
     *
     * @return List of type PointOfInterest containing all POIs received from the DAO.
     */
    @GetMapping("/poi")
    public List<PointOfInterest> getAllPointsOfInterest() {
        return repository.findAll();
    }

    /**
     * Creates a Point of Interest and places it into the database.
     *
     * @param poi Point of Interest to be added into the database.
     * @return POI that was added.
     */
    @Deprecated
    @PostMapping(value = "/poi/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> createPointOfInterest(@RequestBody PointOfInterest poi) throws Exception {

        int id = repository.findAll().size() + 1;
        poi.setId(id);

        repository.save(poi);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(poi.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * Submits a Point of Interest based on information in the form.
     *
     * @param pointOfInterest A POI
     * @return RedirectView which redirects the server to the index page, which will contain the newly created point
     * of interest.
     */
    @PostMapping("/poi/form_create")
    public RedirectView submitPointOfInterest(@ModelAttribute PointOfInterest pointOfInterest) {
        logger.info("HTTP Post Request instantiated from form");
        int id = repository.findAll().size() + 1;
        pointOfInterest.setId(id);
        logger.info("POI set to have ID " + id);

        repository.save(pointOfInterest);
        logger.info("POI saved: \n" + pointOfInterest.toString());
        return new RedirectView("/");
    }

    /**
     * Gets a Point of Interest by its name.
     *
     * @param name Name of the Point of Interest.
     * @return POI if found, null if not.
     */
    @GetMapping("poi/get/name/{name}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<PointOfInterest> getPointOfInterestsByName(@PathVariable("name") String name) {
        return repository.findAllPointsOfInterestByName(name);
    }

    /**
     * Deletes a Point of Interest by its ID.
     *
     * @param id ID pertaining to the Point of Interest.
     * @return POI that was deleted.
     *
     * TODO: change to delete mapping
     */
    @GetMapping("/poi/delete")
    public RedirectView deletePointOfInterestById(@RequestParam("id") long id) {
        repository.deleteById(id);
        return new RedirectView("/admin/list");
    }

    /**
     * Updates a Point of Interest, given its ID.
     *
     * @param poi   The Point Of Interest with its updated members.
     * @param id    The Point of Interest's ID
     * @return  A redirect that indicates the outcome of the method.
     */
    @PostMapping("/poi/update")
    public RedirectView updatePointOfInterest(@ModelAttribute PointOfInterest poi, @RequestParam("id") long id) {
        poi.setId(id);
        repository.save(poi);
        return new RedirectView("/");
    }


}
