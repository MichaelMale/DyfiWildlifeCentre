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

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * PointOfInterestTests.java - Tests to cover the PointOfInterest class.
 *
 * @author Michael Male
 * @version 0.2
 * @see PointOfInterest
 */
public class PointOfInterestTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Utilising the OpenPOJO package, this class test setters and getters.
     *
     * @see com.openpojo
     */
    @Test
    public void validateSettersAndGetters() {
        PojoClass pointOfInterestPojo = PojoClassFactory.getPojoClass(PointOfInterest.class);

        Validator validator = ValidatorBuilder
                .create()
                .with(new SetterMustExistRule())
                .with(new GetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(pointOfInterestPojo);
    }

    /**
     * Tests whether the coordinates method returns the correct coordinates,
     * when cross-referencing a postcodes with the postcodes.io API.
     * @throws IOException  If the client is unable to access the URL.
     */
    @Test
    public void confirmCoordinatesMethodReturnsCorrectCoordinates() throws IOException {
        PointOfInterest test = new PointOfInterest("Houses of Parliament",
                "The " +
                "houses of " +
                "parliament", 0, 0, "SW1A 0AA");
        logger.debug("New object created with a latitude of "
                + test.getLatitude() + " and a longitude of " + test.getLongitude());
        Point2D.Double expectedCoordinates = new Point2D.Double(51.49984,
                -0.124663);
        Point2D.Double result = test.calculateCoordinatesFromPostcode();
        logger.debug("The coordinates calculated from " +
                test.getPostcode() + " was " + result.getX() + "," + result.getY());
        Assertions.assertEquals(expectedCoordinates,
                result);
    }

    @Test
    public void confirmDistanceFromCentreIsZero_WhenQueryingDyfiWildlifeCentre() {
        PointOfInterest dyfiWildlifeCentre = new PointOfInterest("Dyfi " +
                "Wildlife Centre", "Dyfi Wildlife Centre", 52.568774,
                -3.918031, null);
        logger.debug("New object created with a latitude of " + dyfiWildlifeCentre.getLatitude() + " and a longitude of " + dyfiWildlifeCentre.getLongitude());
        double result = dyfiWildlifeCentre.getDistanceFromCentre();
        logger.debug("Distance from centre calculated is: " + result);
        Assertions.assertEquals(0, result);
    }

    @Test
    public void confirmDistanceFromCentreIsNotZero_WhenQueryingLocation() {
        PointOfInterest test = new PointOfInterest("", "", 51.49984,
                -0.124663, "SW1A 0AA");
        logger.debug("New object created with a latitude of " + test.getLatitude()
                + " and a longitude of " + test.getLongitude());
        double distance = test.calculateDistanceFromCentre();
        logger.debug("Distance from centre calculated is: " + distance);
        Assertions.assertEquals(177.3, distance);
    }

    @Test
    public void confirmExceptionIsThrownIfPOIHasAnInvalidPostcode() {
        PointOfInterest test = new PointOfInterest("", "", 0, 0,
                "NOT A POSTCODE");
        logger.debug("New object created with a postcode of " + test.getPostcode());
        PostcodeException thrown = Assertions.assertThrows(
                PostcodeException.class,
                test::calculateCoordinatesFromPostcode,
                "Error while parsing postcode NOT A POSTCODE. Invalid postcode"
        );
        logger.debug("Exception thrown with message " + thrown.getMessage());
    }

    @Test
    public void confirmExceptionIsNotThrownIfPOIHasAValidPostcode() {
        PointOfInterest test = new PointOfInterest("","",0,0,"SY23 3DB");
        logger.debug("New object created with a postcode of " + test.getPostcode());
        Assertions.assertDoesNotThrow(test::calculateCoordinatesFromPostcode,
                "Error while parsing postcode SY23 3DB. Invalid postcode");
    }


}
