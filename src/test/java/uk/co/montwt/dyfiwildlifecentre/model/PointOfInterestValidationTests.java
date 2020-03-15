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

package uk.co.montwt.dyfiwildlifecentre.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

@DataJpaTest
public class PointOfInterestValidationTests {

    @Autowired
    private PointOfInterestRepository repository;

    @Autowired
    private EntityManager manager;

    @Test
    @DisplayName("When a POI with a name set to null is passed to the repository, an exception should be thrown.")
    void whenNameIsNull_thenThrowsException() {
        PointOfInterest nullNamePOI = new PointOfInterest(null, "Description", 0, 0);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    repository.save(nullNamePOI);
                    manager.flush();
                },
                "Error: A name must be specified");
    }

    @Test
    @DisplayName("When a POI with an empty name is passed to the repository, an exception should be thrown.")
    void whenNameIsEmpty_thenThrowsException() {
        PointOfInterest emptyNamePOI = new PointOfInterest("", "Description", 0, 0);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    repository.save(emptyNamePOI);
                    manager.flush();
                },
                "Error: A name must be specified");
    }

    @Test
    @DisplayName("When a POI with a latitude lower than -180 is passed to the repository, an exception should be " +
            "thrown.")
    void whenLatitudeLessThanMinus180_ThenThrowsException() {
        PointOfInterest tooLowLatPOI = new PointOfInterest("Name", "Description", -180.00001, 0);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    repository.save(tooLowLatPOI);
                    manager.flush();
                },
                "Error: Latitude cannot be lower than -180");
    }

    @Test
    @DisplayName("When a POI with a latitude greater than 180 is passed to the repository, an exception should be " +
            "thrown.")
    void whenLatitudeGreaterThan180_ThenThrowsException() {
        PointOfInterest tooHighLatPOI = new PointOfInterest("name", "description", 180.00001, 0);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    repository.save(tooHighLatPOI);
                    manager.flush();
                },
                "Error: Latitude cannot be greater than 180");
    }

    @Test
    @DisplayName("When a POI with a longitude lower than -90 is passed to the repository, an exception should be " +
            "thrown.")
    void whenLongitudeLessThanMinusNinety_ThenThrowsException() {
        PointOfInterest tooLowLonPOI = new PointOfInterest("Name", "Description", 0,
                -90.00001);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    repository.save(tooLowLonPOI);
                    manager.flush();
                },
                "Error: Longitude cannot be lower than -90");
    }

    @Test
    @DisplayName("When a POI with a longitude greater than 90 is passed to the repository, an exception should be " +
            "thrown.")
    void whenLongitudeGreaterThanNinety_ThenThrowsException() {
        PointOfInterest tooHighLonPOI = new PointOfInterest("name", "description", 0,
                90.00001);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
                    repository.save(tooHighLonPOI);
                    manager.flush();
                },
                "Error: Latitude cannot be greater than 90");
    }


}
