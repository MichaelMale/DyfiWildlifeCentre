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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.constraints.NotNull;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * PostcodeService.java - This implements a service that utilises the
 * postcodes.io API. It can be used for validating a postcode as well as
 * converting a postcode to coordinates.
 *
 * @author Michael Male
 * @version 1.0 2020-05-02
 */
public class PostcodeService {

    /*
    Endpoint to the postcodes.io API, a geo-coding API for validation and
    conversion of UK postcodes to coordinates, and vice versa.

    License: MIT License Copyright (c) 2017 Ideal Postcodes
    Source: https://github.com/ideal-postcodes/postcodes.io
     */
    private static final String POSTCODES_API_LINK = "https://api.postcodes" +
            ".io/postcodes/";

    /**
     * Helper method that encodes a postcode to the UTF-8 standard, and
     * replaces whitespace with an ASCII representation of it.
     *
     * @param postcode  String containing the postcode to be encoded.
     * @return  String containing encoded output
     */
    private static String encodePostcode(String postcode) {
        return URLEncoder.encode(postcode, StandardCharsets.UTF_8).replace(
                "+", "%20");
    }

    /**
     * A helper method, that queries the API with a suffix and returns its
     * result.
     *
     * @param suffix    Suffix to query the API.
     * @return  Object of type JsonNode containing the result of such query.
     * @throws IOException  If there is an error when querying the API.
     * @see com.fasterxml.jackson
     */
    private static JsonNode getAPIResult(String suffix) throws IOException {
        URL obj = new URL(POSTCODES_API_LINK + suffix);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in =
                new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        final ObjectNode node =
                new ObjectMapper().readValue(response.toString(),
                        ObjectNode.class);
        return node.get("result");
    }

    /**
     * Checks whether the postcode is a valid postcode.
     *
     * @param postcode  Postcode to be validated.
     * @return  True if the postcode is valid, false if not.
     * @throws IOException  If there is an error when querying the API.
     */
    static boolean validatePostcode(String postcode) throws IOException {
        return getAPIResult(encodePostcode(postcode) + "/validate")
                .asBoolean();
    }

    /**
     * Geocodes a given postcode.
     *
     * @param postcode  String containing the postcode.
     * @return Point2D with a double containing the latitude and the
     *         longitude of the postcode.
     * @throws IOException  If there is an error when querying the API.
     * @throws PostcodeException    If the postcode is invalid.
     */
    static Point2D.Double getCoordinatesFromPostcode(@NotNull String postcode)
            throws IOException, PostcodeException {
        if (!validatePostcode(postcode)) throw new PostcodeException(postcode
                , "Invalid postcode");
        JsonNode result = getAPIResult(encodePostcode(postcode));
        return new Point2D.Double(result.get("latitude").asDouble(),
                result.get("longitude").asDouble());
    }
}
