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

package uk.co.montwt.dyfiwildlifecentre.security;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DyfiAccessDeniedHandler.java - A class that handles an error 403.
 *
 * @author Michael Male
 * @version 0.1  2020-04-12
 * @see AccessDeniedHandler
 */
@Component
public class DyfiAccessDeniedHandler implements AccessDeniedHandler {

    private static Logger logger = LoggerFactory.getLogger(DyfiAccessDeniedHandler.class);

    /**
     * Overrides the handle method. This sends a redirect if a user tried to
     * access a protected URL and couldn't.
     * @param httpServletRequest    Class that provides request information
     *                              for HTTP servlets.
     * @param httpServletResponse   Class that provides response information
     *                              for HTTP servlets.
     * @param e The contents of an AccessDeniedException object.
     * @throws IOException  Thrown if there is an exception in input or
     * output.
     * @throws ServletException Thrown if there is an issue with the HTTP
     * servlet.
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException,
            ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            logger.info("User '" + auth.getName() + "' attempted to access " +
                    "the protected URL: " + httpServletRequest.getRequestURI());
        }

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() +
                "/403");
    }
}
