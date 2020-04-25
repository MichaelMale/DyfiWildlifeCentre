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

package uk.co.montwt.dyfiwildlifecentre.service;

import uk.co.montwt.dyfiwildlifecentre.exception.PointOfInterestNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;

import java.util.List;

/**
 * PointOfInterestService.java - An interface to create a service to manage
 * the Point Of Interest repository.
 */
interface PointOfInterestService {

    /**
     * Finds a Point Of Interest by its ID.
     *
     * @param id    Long containing the ID
     * @return  PointOfInterest that corresponds to this ID
     */
    PointOfInterest findById(Long id) throws PointOfInterestNotFoundException;

    /**
     * Finds all Points of Interest in the database.
     * @return  List of type PointOfInterest containing all Point of Interest
     * in the database.
     */
    List<PointOfInterest> findAll();

    /**
     * Saves a Point of Interest to the database.
     * @param poi   Object of type PointOfInterest containing the POI to be
     *              saved to the database
     */
    void save(PointOfInterest poi);

    /**
     * Finds all Points of Interest that correspond to the name given.
     * @param name  String containing the name to be queried
     * @return  List of type PointOfInterest containing all points of
     * interest by that name
     */
    List<PointOfInterest> findAllPointsOfInterestByName(String name);

    /**
     * Deletes a Point of Interest from the database.
     * @param id    Long containing the ID of the Point of Interest to be
     *              deleted.
     */
    void delete(Long id);
}
