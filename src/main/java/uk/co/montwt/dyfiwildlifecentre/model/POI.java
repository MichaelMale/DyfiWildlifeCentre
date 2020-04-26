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

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Interface for a POI, this is to be used to represent a Point of Interest that will be shown on the map.
 *
 * @author Michael Male
 * @version 0.1
 */
interface POI {

    /**
     * Gets the ID of the Point of Interest.
     *
     * @return ID of the point of interest.
     */
    long getId();

    /**
     * Sets the ID of the Point of Interest.
     *
     * @param id ID of the point of interest.
     */
    void setId(long id);

    /**
     * Gets the name of the Point of Interest.
     *
     * @return String containing the name of the Point Of Interest.
     */
    String getName();

    /**
     * Sets the name for the Point of Interest.
     *
     * @param name String containing the name of the Point of Interest.
     */
    void setName(String name);

    /**
     * Gets the Point of Interest's description
     *
     * @return String containing the description of the Point Of Interest.
     */
    String getDescription();

    /**
     * Sets the description for the Point of Interest.
     *
     * @param description String containing the description of the Point of Interest.
     */
    void setDescription(String description);

    /**
     * Gets the latitude of the Point of Interest.
     *
     * @return double containing the latitude of the Point Of Interest.
     */
    double getLatitude();

    /**
     * Sets the latitude of the Point of Interest.
     *
     * @param latitude double containing the latitude of the Point of Interest.
     */
    void setLatitude(double latitude);

    /**
     * Gets the longitude of the Point of Interest.
     *
     * @return double containing the longitude of the Point of Interest.
     */
    double getLongitude();

    /**
     * Sets the longitude of the Point of Interest.
     *
     * @param longitude double containing the longitude of the Point of Interest.
     */
    void setLongitude(double longitude);

    /**
     * Gets both the latitude and longitude of the Point of Interest.
     *
     * @return A Point2D object that contains a latitude and longitude in double floating-point precision.
     */
    Point2D.Double generateCoordinates();

    /**
     * Calculates the distance between the given coordinates and the
     * coordinates of the Dyfi Wildlife Centre.
     * @return 0 if the object is the Dyfi Wildlife Centre, double containing
     * distance if not.
     */
    double calculateDistanceFromCentre();

    /**
     * Returns the Point of Interest in the JSON format.
     *
     * @return String containing Point of Interest details in a JSON format.
     */
    String toJSON();

    /**
     * Compares two PointOfInterest objects by all members of the class.
     *
     * @param obj Object to be compared
     * @return true if the same, false if not
     */
    @Override
    boolean equals(Object obj);

    /**
     * Generates the hash code value of the object.
     *
     * @return int containing the hash code value of the object on which this method is invoked.
     */
    @Override
    int hashCode();

    /**
     * Sets the postcode.
     * @param postcode  String containing a postcode
     */
    void setPostcode(String postcode);

    /**
     * Gets the postcode.
     * @return  String containing a postcode
     */
    String getPostcode();

    /**
     * Calculates the coordinates from a UK postcode, utilising the Postcode
     * API.
     * @return Point2D.Double containing the latitude and the longitude of
     * the coordinates, calculated by its postcode.
     */
    Point2D.Double calculateCoordinatesFromPostcode() throws IOException;


}
