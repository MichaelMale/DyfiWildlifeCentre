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
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;

public class PointOfInterestTests {

    private final static Logger logger =
            LoggerFactory.getLogger(PointOfInterestTests.class);

    @Test
    public void validateSettersAndGetters() {
        PojoClass pointOfInterestPojo =
                PojoClassFactory.getPojoClass(PointOfInterest.class);
        Validator validator = ValidatorBuilder.create()
        // checks that setter and getter is present for
        // every field
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
        // Validate that setters and getters are behaving as expected
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        // Start test
        validator.validate(pointOfInterestPojo);
    }

    @Test
    public void validateCorrectDistanceFromCentre() {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setCoordinates(new Point2D.Double(52.568774,-3.918031));
        logger.debug("New POI created with coordinates " +
                pointOfInterest.generateCoordinates().toString());
        Assertions.assertEquals(0,pointOfInterest.getDistanceFromCentre());
    }

    @Test
    public void validateNonZeroDistanceFromCentre() {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setCoordinates(new Point2D.Double(0,0));
        logger.debug("New POI created with coordinates " +
                pointOfInterest.generateCoordinates().toString());
        Assertions.assertEquals(3639.0,
                pointOfInterest.calculateDistanceFromCentre());
    }



}
