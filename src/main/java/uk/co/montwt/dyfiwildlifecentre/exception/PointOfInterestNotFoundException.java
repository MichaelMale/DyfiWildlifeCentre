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

package uk.co.montwt.dyfiwildlifecentre.exception;

/**
 * PointOfInterestNotFoundException.java - A custom exception that is thrown
 * when a POI is not found.
 *
 * @author Michael Male
 * @version 1.0 2020-04-12
 * @see RuntimeException
 */
public class PointOfInterestNotFoundException extends RuntimeException {

    /**
     * Constructor for objects of type PointOfInterestNotFoundException,
     * returning a relevant message.
     * @param id    The ID entered, that could not be correlated with a Point
     *             Of Interest.
     */
    public PointOfInterestNotFoundException(long id) {
        super("Could not find POI " + id);
    }
}
