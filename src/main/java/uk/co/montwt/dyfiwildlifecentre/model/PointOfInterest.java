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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PointOfInterest.java - This class represents a Point of Interest. It will be used whenever an end user would like
 * to add somewhere of interest that they would like shown on the Google map. It contains the name of the POI, its
 * description, and its coordinates. Currently, this class represents an object that will be statically created.
 * Further iterations will involve linking it to a PostgreSQL database, and including filters.
 *
 * @author Michael Male
 * @version 0.2 2020-04-12
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

    @Min(value = -90, message = "Error: Latitude cannot be lower than -90")
    @Max(value = 90, message = "Error: Latitude cannot be greater than 90")
    private double latitude;

    @Min(value = -180, message = "Error: Longitude cannot be lower than -180")
    @Max(value = 180, message = "Error: Longitude cannot be greater than 180")
    private double longitude;

    @Column(nullable = true)
    private String postcode;

    @Column(name = "distance_from_centre")
    private double distanceFromCentre;

    /**
     * Default Constructor for objects of type PointOfInterest.
     */
    public PointOfInterest() {
    }

    /**
     * Overloaded constructor for objects of type PointOfInterest.
     * @param name  The name of the Point Of Interest.
     * @param description   The description of the Point Of Interest.
     * @param latitude  The latitude of the Point Of Interest.
     * @param longitude The longitude of the Point Of Interest.
     * @param postcode  The postcode of the Point of Interest.
     */
    public PointOfInterest(String name, String description, double latitude,
                           double longitude, String postcode) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postcode = postcode;
    }

    public double getDistanceFromCentre() {
        return distanceFromCentre;
    }

    public void setDistanceFromCentre() {
        this.distanceFromCentre = this.calculateDistanceFromCentre();
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
     * Calculates the distance between the given coordinates and the
     * coordinates of the Dyfi Wildlife Centre. This is calculated using the
     * Haversine formula.
     *
     * Code adapted from
     * <a href="https://rosettacode.org/wiki/Haversine_formula#Java">here</a>.
     *
     * @return double containing the distance in miles to 4 significant
     * figures.
     */
    @Override
    public double calculateDistanceFromCentre() {
        final double EARTH_RADIUS_MILES = 3958.8; // Approximate radius of
        // Earth in miles, used to calculate the distance.

        /* Local variables used for cleaner code */
        double dyfiLat = 52.568774;
        double dyfiLng = -3.918031;

        double currentLat = this.getLatitude();
        double currentLng = this.getLongitude();

        if ((dyfiLat == currentLat) && (dyfiLng == currentLng)) {
            return 0; // If there is no difference between both coordinates,
            // distance of 0 is returned, to avoid unnecessary calculation.
        } else {
            double dLat = Math.toRadians(currentLat - dyfiLat);
            double dLng = Math.toRadians(currentLng - dyfiLng);
            dyfiLat = Math.toRadians(dyfiLat);
            currentLat = Math.toRadians(currentLat);

            double a =
                    Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLng / 2), 2)
                    * Math.cos(dyfiLat) * Math.cos(currentLat);
            double c = 2 * Math.asin(Math.sqrt(a));
            double result = (EARTH_RADIUS_MILES * c);
            /* Converts result into a BigDecimal that is then rounded to 4
            significant figures.
             */
            MathContext mathContext = new MathContext(4, RoundingMode.DOWN);
            BigDecimal bigDecimal = new BigDecimal(result, mathContext);
            return bigDecimal.doubleValue();
        }

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

    /**
     * Sets the postcode.
     *
     * @param postcode String containing a postcode
     */
    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * Gets the postcode.
     *
     * @return String containing a postcode
     */
    @Override
    public String getPostcode() {
        return this.postcode;
    }

    /**
     * Calculates the coordinates from a UK postcode, utilising the Postcode
     * API.
     *
     * @return Point2D.Double containing the latitude and the longitude of
     * the coordinates, calculated by its postcode.
     * @see <a href="https://postcodes.io">postcodes.io</a>
     */
    @Override
    public Point2D.Double calculateCoordinatesFromPostcode() throws IOException {
        String postcodeRegex = "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1," +
                "2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([AZa-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))[0-9][A-Za-z]{2})$";
        Pattern postcodePattern = Pattern.compile(postcodeRegex);
        Matcher matcher = postcodePattern.matcher(this.getPostcode());

        if (!matcher.matches()) {
            throw new IOException("Invalid postcode");
        }

        final String encodedPostcode = URLEncoder.encode(this.getPostcode(),
                StandardCharsets.UTF_8).replace("+", "%20");
        final String baseUrl = "https://api.postcodes.io/postcodes/";

        URL obj = new URL(baseUrl + encodedPostcode);

        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        final ObjectNode node =
                new ObjectMapper().readValue(response.toString(),
                        ObjectNode.class);
        final JsonNode result = node.get("result");

        return new Point2D.Double(result.get("latitude").asDouble(),
                result.get("longitude").asDouble());
    }

    /**
     * Sets both latitude and longitude using a Point2D.Double object.
     *
     * @param coordinates Point2D.Double, where x is the latitude and y is
     *                    the longitude.
     */
    @Override
    public void setCoordinates(Point2D.Double coordinates) {
        this.setLatitude(coordinates.getX());
        this.setLongitude(coordinates.getY());
    }

    /**
     * Used to provide a String representation of a Point Of Interest.
     * @return  String containing a JSON representation of a POI.
     */
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(this);
    }
}
