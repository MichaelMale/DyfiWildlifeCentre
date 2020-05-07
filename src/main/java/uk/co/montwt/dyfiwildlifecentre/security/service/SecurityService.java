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

package uk.co.montwt.dyfiwildlifecentre.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Creates a service that provides details on security, this is used further
 * within the controller.
 *
 * @author Michael Male
 * @version 1.0 2020-04-20
 */
@Service
public class SecurityService {
    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    /**
     * Constructor for objects of type SecurityService.
     * @param authenticationManager An autowired object of type
     *                              AuthenticationManager
     * @param userDetailsService    An autowired object of type
     *                              UserDetailsService that utilises a
     *                              qualifier defining its implementation
     */
    @Autowired
    public SecurityService(AuthenticationManager
                                       authenticationManager,
                           @Qualifier("userDetailsServiceImpl")
                                   UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Automatically logs in a user after they have been registered.
     * @param username  Username to use for the login
     * @param password  Password to use for the login
     */
    public void autoLogin(String username, String password) {
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, password,
                        userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);
        }
    }
}
