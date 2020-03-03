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


import org.springframework.web.bind.annotation.*;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;

import java.util.List;

@RestController
public class PointOfInterestController implements POIControllerInterface {


    /**
     * Gets a POI by its ID.
     *
     * @param id ID of the POI.
     * @return POI that relates to this ID, null if not found.
     */
    @Override
    @GetMapping("/poi/get/id/{id}")
    public PointOfInterest getPointOfInterestById(@PathVariable("id") long id) {
        return null;
    }

    /**
     * Gets all Points of Interest in the DAO.
     *
     * @return List of type PointOfInterest containing all POIs received from the DAO.
     */
    @Override
    @GetMapping("/poi/getAll")
    public List<PointOfInterest> getAllPointsOfInterest() {
        return null;
    }

    /**
     * Creates a Point of Interest and places it into the database.
     *
     * @param poi Point of Interest to be added into the database.
     * @return POI that was added.
     */
    @Override
    @GetMapping("/poi/create")
    public PointOfInterest createPointOfInterest(@RequestBody PointOfInterest poi) {
        return null;
    }

    /**
     * Gets a Point of Interest by its name.
     *
     * @param name Name of the Point of Interest.
     * @return POI if found, null if not.
     */
    @Override
    @GetMapping("poi/get/name/{name}")
    public List<PointOfInterest> getPointOfInterestsByName(@PathVariable("name") String name) {
        return null;
    }

    /**
     * Deletes a Point of Interest by its ID.
     *
     * @param id ID pertaining to the Point of Interest.
     * @return POI that was deleted.
     */
    @Override
    @DeleteMapping("/poi/delete/{id}")
    public PointOfInterest deletePointOfInterestById(@PathVariable("id") long id) {
        return null;
    }
}
