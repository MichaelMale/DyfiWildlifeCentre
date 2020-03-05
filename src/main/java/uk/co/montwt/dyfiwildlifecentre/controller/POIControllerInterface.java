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

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;

import java.util.List;

@RestController
public interface POIControllerInterface {


    /**
     * Gets a POI by its ID.
     *
     * @param id ID of the POI.
     * @return POI that relates to this ID, null if not found.
     */
    @GetMapping("/poi/get/{id}")
    PointOfInterest getPointOfInterestById(@PathVariable("id") long id);

    /**
     * Gets all Points of Interest in the DAO.
     *
     * @return List of type PointOfInterest containing all POIs received from the DAO.
     */
    @GetMapping("/poi/get/all")
    List<PointOfInterest> getAllPointsOfInterest();

    /**
     * Creates a Point of Interest and places it into the database.
     *
     * @param poi Point of Interest to be added into the database.
     * @return POI that was added.
     */
    @PostMapping(value = "/poi/create", consumes = "application/json", produces = "application/json")
    ResponseEntity<Object> createPointOfInterest(@RequestBody PointOfInterest poi) throws Exception;

    /**
     * Gets a Point of Interest by its name.
     *
     * @param name Name of the Point of Interest.
     * @return POI if found, null if not.
     */
    @GetMapping("/poi/get/{name}")
    List<PointOfInterest> getPointOfInterestsByName(@PathVariable("name") String name);

    /**
     * Deletes a Point of Interest by its ID.
     *
     * @param id ID pertaining to the Point of Interest.
     * @return POI that was deleted.
     */
    @DeleteMapping("/poi/delete/{id}")
    void deletePointOfInterestById(@PathVariable("id") long id);


}
