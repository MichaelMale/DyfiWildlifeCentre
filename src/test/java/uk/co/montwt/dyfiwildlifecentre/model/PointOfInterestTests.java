/*
 * Copyright (C) 2020 Michael Male
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package uk.co.montwt.dyfiwildlifecentre.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.Set;

public class PointOfInterestTests {

    private static jakarta.validation.Validator beanValidator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        beanValidator = factory.getValidator();
    }

    @Test
    public void validateSettersAndGetters() {
        PojoClass pointOfInterestPojo =
                PojoClassFactory.getPojoClass(PointOfInterest.class);
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(pointOfInterestPojo);
    }

    @Test
    public void validateOverloadedConstructorAndGenerateCoordinates() {
        PointOfInterest poi = new PointOfInterest(
                "Osprey Nest",
                "Main nesting platform",
                52.5695,
                -3.9175,
                "SY20 8SR");

        Assertions.assertEquals("Osprey Nest", poi.getName());
        Assertions.assertEquals("Main nesting platform", poi.getDescription());
        Assertions.assertEquals(52.5695, poi.getLatitude());
        Assertions.assertEquals(-3.9175, poi.getLongitude());
        Assertions.assertEquals("SY20 8SR", poi.getPostcode());
        Assertions.assertEquals(new Point2D.Double(52.5695, -3.9175), poi.generateCoordinates());
    }

    @Test
    public void validateCorrectDistanceFromCentre() {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setCoordinates(new Point2D.Double(52.568774, -3.918031));
        Assertions.assertEquals(0, pointOfInterest.calculateDistanceFromCentre());
    }

    @Test
    public void validateNonZeroDistanceFromCentre() {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setCoordinates(new Point2D.Double(0, 0));
        Assertions.assertEquals(3639.0, pointOfInterest.calculateDistanceFromCentre());
    }

    @Test
    public void validateNearbyDistanceCalculationIsPositiveAndRounded() {
        PointOfInterest machynlleth = new PointOfInterest();
        machynlleth.setCoordinates(new Point2D.Double(52.5906, -3.8511));
        double distance = machynlleth.calculateDistanceFromCentre();
        Assertions.assertTrue(distance > 2.5 && distance < 4.0,
                "Expected Machynlleth distance ~3.18 miles, got " + distance);
    }

    @Test
    public void validateJsonAndToStringSerialization() {
        PointOfInterest poi = new PointOfInterest("Hide", "Viewing hide", 52.57, -3.91, "");
        poi.setId(42L);
        poi.setCategory("wildlife");
        poi.setDistanceFromCentre(0.35);
        poi.setNameCy("Cuddfan");
        poi.setDescriptionCy("Cuddfan wylio");
        poi.setImageUrl("/uploads/hide.jpg");
        poi.setStationCode("DVY");
        poi.setBusStopCode("5230AWD71582");

        Assertions.assertTrue(poi.isRailwayStation());
        Assertions.assertTrue(poi.isBusStop());
        Assertions.assertTrue(poi.isProtectedTransport());
        poi.setStationCode("   ");
        Assertions.assertFalse(poi.isRailwayStation());
        Assertions.assertTrue(poi.isProtectedTransport());
        poi.setBusStopCode("   ");
        Assertions.assertFalse(poi.isBusStop());
        Assertions.assertFalse(poi.isProtectedTransport());
        poi.setStationCode("DVY");
        poi.setBusStopCode("5230AWD71582");

        JsonObject json = JsonParser.parseString(poi.toJSON()).getAsJsonObject();
        Assertions.assertEquals(42L, json.get("id").getAsLong());
        Assertions.assertEquals("Hide", json.get("name").getAsString());
        Assertions.assertEquals("wildlife", json.get("category").getAsString());
        Assertions.assertEquals("Cuddfan", json.get("nameCy").getAsString());
        Assertions.assertEquals("Cuddfan wylio", json.get("descriptionCy").getAsString());
        Assertions.assertEquals("/uploads/hide.jpg", json.get("imageUrl").getAsString());
        Assertions.assertEquals("DVY", json.get("stationCode").getAsString());
        Assertions.assertEquals("5230AWD71582", json.get("busStopCode").getAsString());

        JsonObject prettyJson = JsonParser.parseString(poi.toString()).getAsJsonObject();
        Assertions.assertEquals(json, prettyJson);
    }

    @Test
    public void validateEqualsAndHashCodeById() {
        PointOfInterest first = new PointOfInterest("A", "desc", 52.5, -3.9, "");
        first.setId(7L);

        PointOfInterest sameId = new PointOfInterest("B", "other", 51.0, -2.0, "");
        sameId.setId(7L);

        PointOfInterest differentId = new PointOfInterest("A", "desc", 52.5, -3.9, "");
        differentId.setId(8L);

        Assertions.assertEquals(first, first);
        Assertions.assertEquals(first, sameId);
        Assertions.assertEquals(first.hashCode(), sameId.hashCode());
        Assertions.assertNotEquals(first, differentId);
        Assertions.assertNotEquals(first, null);
        Assertions.assertNotEquals(first, "not-a-poi");
    }

    @Test
    public void validateBeanValidationConstraints() {
        PointOfInterest invalid = new PointOfInterest("", "desc", 95.0, -185.0, "");
        Set<ConstraintViolation<PointOfInterest>> violations = beanValidator.validate(invalid);
        Assertions.assertEquals(3, violations.size());

        PointOfInterest valid = new PointOfInterest("Osprey Hide", "desc", 52.5687, -3.918, "");
        Assertions.assertTrue(beanValidator.validate(valid).isEmpty());
    }
}
