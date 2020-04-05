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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * PointOfInterest.java - This class represents a Point of Interest. It will be used whenever an end user would like
 * to add somewhere of interest that they would like shown on the Google map. It contains the name of the POI, its
 * description, and its coordinates. Currently, this class represents an object that will be statically created.
 * Further iterations will involve linking it to a PostgreSQL database, and including filters.
 *
 * @author Michael Male
 * @version 0.1
 * @see POI Interface that this class implements
 */

@Entity
@Table(name = "points_of_interest")
public class PointOfInterest implements POI {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty(message = "Error: A name must be specified")
    private String name;

    private String description;

    @Min(value = -180, message = "Error: Latitude cannot be lower than -180")
    @Max(value = 180, message = "Error: Latitude cannot be greater than 180")
    private double latitude;

    @Min(value = -90, message = "Error: Longitude cannot be lower than -90")
    @Max(value = 90, message = "Error: Longitude cannot be greater than 90")
    private double longitude;

    public PointOfInterest() {
    }

    public PointOfInterest(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the ID of the Point of Interest.
     *
     * @return ID of the point of interest.
     */
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Sets the ID of the Point of Interest.
     *
     * @param id ID of the point of interest.
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the name of the Point of Interest.
     *
     * @return String containing the name of the Point Of Interest.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name for the Point of Interest.
     *
     * @param name String containing the name of the Point of Interest.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Point of Interest's description
     *
     * @return String containing the description of the Point Of Interest.
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for the Point of Interest.
     *
     * @param description String containing the description of the Point of Interest.
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the latitude of the Point of Interest.
     *
     * @return double containing the latitude of the Point Of Interest.
     */
    @Override
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Sets the latitude of the Point of Interest.
     *
     * @param latitude double containing the latitude of the Point of Interest.
     */
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of the Point of Interest.
     *
     * @return double containing the longitude of the Point of Interest.
     */
    @Override
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Sets the longitude of the Point of Interest.
     *
     * @param longitude double containing the longitude of the Point of Interest.
     */
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets both the latitude and longitude of the Point of Interest.
     *
     * @return A Point2D object that contains a latitude and longitude in double floating-point precision.
     */
    @Override
    public Point2D.Double generateCoordinates() {
        return new Point2D.Double(this.getLatitude(), this.getLongitude());
    }

    /**
     * Returns the Point of Interest in the JSON format.
     *
     * @return String containing Point of Interest details in a JSON format.
     */
    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Compares one PointOfInterest to another to check if it is the same. This method compares solely by its ID.
     *
     * @param o The object to be compared to the object this method was called on
     * @return boolean: true if it is equal to the other object, false if it is not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointOfInterest that = (PointOfInterest) o;
        return getId() == that.getId();
    }

    /**
     * A hash representation of an object by its ID.
     *
     * @return Hash containing a representation of the object it is called on.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(this);
    }
}
