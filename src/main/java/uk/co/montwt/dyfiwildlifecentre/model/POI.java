package uk.co.montwt.dyfiwildlifecentre.model;

import java.awt.geom.Point2D;

/**
 * Interface for a POI, this is to be used to represent a Point of Interest that will be shown on the map.
 *
 * @author Michael Male
 * @version 0.1
 */
interface POI {

    /**
     * Gets the name of the Point of Interest.
     * @return  String containing the name of the Point Of Interest.
     */
    String getName();

    /**
     * Gets the Point of Interest's description
     * @return  String containing the description of the Point Of Interest.
     */
    String getDescription();

    /**
     * Gets the latitude of the Point of Interest.
     * @return  double containing the latitude of the Point Of Interest.
     */
    double getLatitude();

    /**
     * Gets the longitude of the Point of Interest.
     * @return  double containing the longitude of the Point of Interest.
     */
    double getLongitude();

    /**
     * Sets the name for the Point of Interest.
     * @param name  String containing the name of the Point of Interest.
     */
    void setName(String name);

    /**
     * Sets the description for the Point of Interest.
     * @param description   String containing the description of the Point of Interest.
     */
    void setDescription(String description);

    /**
     * Sets the latitude of the Point of Interest.
     * @param latitude  double containing the latitude of the Point of Interest.
     */
    void setLatitude(double latitude);

    /**
     * Sets the longitude of the Point of Interest.
     * @param longitude double containing the longitude of the Point of Interest.
     */
    void setLongitude(double longitude);

    /**
     * Gets both the latitude and longitude of the Point of Interest.
     * @return  A Point2D object that contains a latitude and longitude in double floating-point precision.
     */
    Point2D getCoordinates();

    /**
     * Returns the Point of Interest in the GeoJSON format.
     * @return  String containing Point of Interest details in a GeoJSON format.
     */
    String toGeoJSON();

    /**
     * Compares two PointOfInterest objects by all members of the class.
     * @param obj   Object to be compared
     * @return  true if the same, false if not
     */
    @Override
    boolean equals(Object obj);

    /**
     * Generates the hash code value of the object.
     * @return  int containing the hash code value of the object on which this method is invoked.
     */
    @Override
    int hashCode();


}
