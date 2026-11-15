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

package uk.co.montwt.dyfiwildlifecentre.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostcodeServiceTests {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @AfterEach
    void tearDown() {
        PostcodeService.resetApiClient();
    }

    @Test
    public void testPostcodeValidationOnTruePostcode() throws IOException {
        List<String> suffixes = new ArrayList<>();
        PostcodeService.setApiClient(suffix -> {
            suffixes.add(suffix);
            return MAPPER.readTree("true");
        });

        Assertions.assertTrue(PostcodeService.validatePostcode("sw1a1aa"));
        Assertions.assertEquals(List.of("sw1a1aa/validate"), suffixes);
    }

    @Test
    public void testPostcodeValidationOnIncorrectPostcode() throws IOException {
        PostcodeService.setApiClient(suffix -> MAPPER.readTree("false"));

        Assertions.assertFalse(PostcodeService.validatePostcode("zz011aa"));
    }

    @Test
    public void testPostcodeValidationOnNullOrBlankReturnsFalseWithoutApiCall() throws IOException {
        PostcodeService.setApiClient(suffix -> {
            throw new AssertionError("API should not be called for null/blank postcode");
        });

        Assertions.assertFalse(PostcodeService.validatePostcode(null));
        Assertions.assertFalse(PostcodeService.validatePostcode("   "));
    }

    @Test
    public void confirmValidCoordinatesFromPostcode() throws IOException {
        List<String> suffixes = new ArrayList<>();
        PostcodeService.setApiClient(suffix -> {
            suffixes.add(suffix);
            if (suffix.endsWith("/validate")) {
                return MAPPER.readTree("true");
            }
            return MAPPER.readTree("{\"latitude\":51.50101,\"longitude\":-0.141563}");
        });

        Point2D.Double coords = PostcodeService.getCoordinatesFromPostcode("SW1A 1AA");
        Assertions.assertEquals(new Point2D.Double(51.50101, -0.141563), coords);
        Assertions.assertEquals(List.of("SW1A%201AA/validate", "SW1A%201AA"), suffixes);
    }

    @Test
    public void confirmExceptionThrownIfPostcodeInvalid() {
        PostcodeService.setApiClient(suffix -> MAPPER.readTree("false"));

        PostcodeException thrown = Assertions.assertThrows(
                PostcodeException.class,
                () -> PostcodeService.getCoordinatesFromPostcode("zz011aa"),
                "Expected getCoordinatesFromPostcode() to throw, but it didn't");

        Assertions.assertEquals(
                "Error while parsing postcode zz011aa. Invalid postcode",
                thrown.getMessage());
    }

    @Test
    public void confirmExceptionThrownIfCoordinatesMissingFromApiResponse() {
        PostcodeService.setApiClient(suffix -> {
            if (suffix.endsWith("/validate")) {
                return MAPPER.readTree("true");
            }
            return MAPPER.readTree("{\"latitude\":null,\"longitude\":null}");
        });

        PostcodeException thrown = Assertions.assertThrows(
                PostcodeException.class,
                () -> PostcodeService.getCoordinatesFromPostcode("SY20 8SR"));

        Assertions.assertEquals(
                "Error while parsing postcode SY20 8SR. Coordinates unavailable",
                thrown.getMessage());
    }

    @Test
    public void confirmIOExceptionPropagatesFromApiClient() {
        PostcodeService.setApiClient(suffix -> {
            throw new IOException("Network failure");
        });

        Assertions.assertThrows(IOException.class, () -> PostcodeService.validatePostcode("SY20 8SR"));
    }
}
