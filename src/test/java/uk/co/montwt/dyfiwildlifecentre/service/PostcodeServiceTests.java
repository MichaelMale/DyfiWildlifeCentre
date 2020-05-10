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

package uk.co.montwt.dyfiwildlifecentre.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;

import java.awt.geom.Point2D;
import java.io.IOException;

public class PostcodeServiceTests {

    private final static Logger logger =
            LoggerFactory.getLogger(PostcodeServiceTests.class);

    @Test
    public void testPostcodeValidationOnTruePostcode() throws IOException {
        logger.debug("Testing that postcode \"sw1a1aa\" is a valid postcode.");
        Assertions.assertTrue(PostcodeService.validatePostcode("sw1a1aa"));
    }

    @Test
    public void testPostcodeValidationOnIncorrectPostcode() throws IOException {
        logger.debug("Testing that postcode \"zz011aa\" is an invalid " +
                "postcode.");
        Assertions.assertFalse(PostcodeService.validatePostcode("zz011aa"));
    }

    @Test
    public void confirmValidCoordinatesFromPostcode() throws IOException {
        logger.debug("Testing that coordinates \"51.501009,-0.141588\" " +
                "correspond to postcode \"SW1A 1AA\"");
        Assertions.assertEquals(new Point2D.Double(51.501009,-0.141588),
                PostcodeService.getCoordinatesFromPostcode("SW1A 1AA"));
    }

    @Test
    public void confirmExceptionThrownIfPostcodeInvalid() {
        logger.debug("Testing than an exception is thrown if a method call to" +
                " getCoordinates() is made with an invalid postcode.");
        PostcodeException thrown =
                Assertions.assertThrows(PostcodeException.class,
                () -> PostcodeService.getCoordinatesFromPostcode("zz011aa"),
            "Expected getCoordinates() to throw, but it didn't");

        logger.debug("Testing that the exception message contains the invalid" +
                " postcode and the reasoning behind the exception.");
        Assertions.assertEquals(thrown.getMessage(), "Error while parsing" +
                " postcode zz011aa. Invalid postcode");
    }
}
