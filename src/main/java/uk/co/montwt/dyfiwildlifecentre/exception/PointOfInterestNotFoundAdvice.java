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

package uk.co.montwt.dyfiwildlifecentre.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * PointOfInterestNotFoundAdvice.java - A class that provides a message for a
 * PointOfInterestNotFoundException
 *
 * @author Michael Male
 * @version 1.0 2020-04-12
 * @see PointOfInterestNotFoundException
 */
@ControllerAdvice
public class PointOfInterestNotFoundAdvice {

    /**
     * Handles an exception that the POI is not found.
     * @param ex    The exception that is being handled.
     * @return  String containing the exception's message.
     */
    @ResponseBody
    @ExceptionHandler(PointOfInterestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pointOfInterestNotFoundHandler(PointOfInterestNotFoundException ex) {
        return ex.getMessage();
    }
}
