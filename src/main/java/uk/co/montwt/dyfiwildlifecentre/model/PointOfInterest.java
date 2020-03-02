package uk.co.montwt.dyfiwildlifecentre.model;

import com.google.gson.Gson;

import java.awt.geom.Point2D;

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
public class PointOfInterest implements POI {

    private String name;
    private String description;
    private double latitude;
    private double longitude;

    public PointOfInterest(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
