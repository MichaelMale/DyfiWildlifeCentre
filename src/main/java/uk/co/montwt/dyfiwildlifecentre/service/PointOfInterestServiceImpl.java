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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.montwt.dyfiwildlifecentre.exception.PointOfInterestNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterestRepository;

import java.util.List;

/**
 * PointOfInterestServiceImpl.java - Implements the service interface,
 * provides a layer of abstraction between the controller and the repository.
 *
 * @author Michael Male
 * @version 0.1     2020-04-24
 * @see PointOfInterestService
 */
@Service
public class PointOfInterestServiceImpl implements PointOfInterestService {

    private final PointOfInterestRepository repository;

    @Autowired
    public PointOfInterestServiceImpl(PointOfInterestRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds a Point Of Interest by its ID.
     *
     * @param id Long containing the ID
     * @return PointOfInterest that corresponds to this ID
     * @throws PointOfInterestNotFoundException if the POI has not been found
     */
    @Override
    public PointOfInterest findById(Long id) throws PointOfInterestNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new PointOfInterestNotFoundException(id));
    }

    /**
     * Finds all Points of Interest in the database.
     *
     * @return List of type PointOfInterest containing all Point of Interest
     * in the database.
     */
    @Override
    public List<PointOfInterest> findAll() {
        return repository.findAll();
    }

    /**
     * Saves a Point of Interest to the database.
     *
     * @param poi Object of type PointOfInterest containing the POI to be
     *            saved to the database
     */
    @Override
    public void save(PointOfInterest poi) {
        repository.save(poi);
    }

    /**
     * Finds all Points of Interest that correspond to the name given.
     *
     * @param name String containing the name to be queried
     * @return List of type PointOfInterest containing all points of
     * interest by that name
     */
    @Override
    public List<PointOfInterest> findAllPointsOfInterestByName(String name) {
        return repository.findAllPointsOfInterestByName(name);
    }

    /**
     * Deletes a Point of Interest from the database.
     *
     * @param id Long containing the ID of the Point of Interest to be
     *           deleted.
     */
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
