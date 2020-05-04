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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.co.montwt.dyfiwildlifecentre.exception.PointOfInterestNotFoundException;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * PointOfInterestServiceImpl.java - Implements the service interface,
 * provides a layer of abstraction between the controller and the repository.
 *
 * @author Michael Male
 * @version 0.2     2020-04-28
 * @see PointOfInterestService
 */
@Service
public class PointOfInterestServiceImpl implements PointOfInterestService {

    private final PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public PointOfInterestServiceImpl(PointOfInterestRepository pointOfInterestRepository) {
        this.pointOfInterestRepository = pointOfInterestRepository;
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
        return pointOfInterestRepository.findById(id)
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
        return pointOfInterestRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    /**
     * Saves a Point of Interest to the database.
     *
     * @param poi Object of type PointOfInterest containing the POI to be
     *            saved to the database
     */
    @Override
    public void save(PointOfInterest poi) throws IOException {
        // Error checking code. Checks firstly if latitude and longitude are
        // zero, then checks if both were entered. Also checks if latitude
        // but not longitude was entered and vice versa.
        if (poi.getLatitude() == 0 && poi.getLongitude() == 0) {
            if (!poi.getPostcode().isEmpty()) {
                poi.setCoordinates(PostcodeService
                        .getCoordinatesFromPostcode(poi.getPostcode()));
            }
        } else if (!poi.getPostcode().isEmpty()) {
            throw new PostcodeException(poi.getPostcode(),
                    "Both postcode and coordinates were entered.");
        }


        poi.setDistanceFromCentre(poi.calculateDistanceFromCentre());
        pointOfInterestRepository.save(poi);
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
        return pointOfInterestRepository.findAllPointsOfInterestByName(name);
    }

    /**
     * Deletes a Point of Interest from the database.
     *
     * @param id Long containing the ID of the Point of Interest to be
     *           deleted.
     */
    @Override
    public void delete(Long id) {
        pointOfInterestRepository.deleteById(id);
    }

    /**
     * Updates a Point of Interest from the database
     *
     * @param poi Point of Interest to be updated.
     */
    @Override
    public void update(PointOfInterest poi) {
        poi.setDistanceFromCentre(poi.calculateDistanceFromCentre());
        pointOfInterestRepository.save(poi);
    }


}
