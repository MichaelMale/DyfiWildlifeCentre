package uk.co.montwt.dyfiwildlifecentre.model;

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
        return null;
    }

    /**
     * Gets the Point of Interest's description
     *
     * @return String containing the description of the Point Of Interest.
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * Gets the latitude of the Point of Interest.
     *
     * @return double containing the latitude of the Point Of Interest.
     */
    @Override
    public double getLatitude() {
        return 0;
    }

    /**
     * Gets the longitude of the Point of Interest.
     *
     * @return double containing the longitude of the Point of Interest.
     */
    @Override
    public double getLongitude() {
        return 0;
    }

    /**
     * Sets the name for the Point of Interest.
     *
     * @param name String containing the name of the Point of Interest.
     */
    @Override
    public void setName(String name) {

    }

    /**
     * Sets the description for the Point of Interest.
     *
     * @param description String containing the description of the Point of Interest.
     */
    @Override
    public void setDescription(String description) {

    }

    /**
     * Sets the latitude of the Point of Interest.
     *
     * @param latitude double containing the latitude of the Point of Interest.
     */
    @Override
    public void setLatitude(double latitude) {

    }

    /**
     * Sets the longitude of the Point of Interest.
     *
     * @param longitude double containing the longitude of the Point of Interest.
     */
    @Override
    public void setLongitude(double longitude) {

    }

    /**
     * Gets both the latitude and longitude of the Point of Interest.
     *
     * @return A Point2D object that contains a latitude and longitude in double floating-point precision.
     */
    @Override
    public Point2D getCoordinates() {
        return null;
    }

    /**
     * Returns the Point of Interest in the GeoJSON format.
     *
     * @return String containing Point of Interest details in a GeoJSON format.
     */
    @Override
    public String toGeoJSON() {
        return null;
    }
}
