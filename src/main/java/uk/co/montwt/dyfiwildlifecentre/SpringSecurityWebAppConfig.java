
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

package uk.co.montwt.dyfiwildlifecentre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * SpringSecurityWebAppConfig.java - This class overrides a Spring Security
 * method to add parameters that permit requests and vice versa. It utilises
 * a login form to ensure that only authenticated users can access the admin
 * area, as well as perform POST requests.
 *
 * @author Michael Male
 * @version 0.1 2020-04-12
 * @see WebSecurityConfigurerAdapter
 */
@Configuration
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    /* TODO: Check why automatic field injection is not recommended */
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * Configures Spring Security, by allowing access to one GET request and
     * the index, and disallowing unauthenticated access to the admin panel.
     *
     * @param http Object of type HttpSecurity that holds parameters
     *             pertinent to the configuration of web based security for
     *             specific http request. It is similar to an XML element in
     *             namespace configuration.
     * @throws Exception    If there is an error in the Spring Security
     * configuration.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/index").permitAll() // Permits all
                // requests to the single-page user-end application
                .antMatchers("/poi").permitAll() // Permits a GET Request to
                // the getAllPointsOfInterest() method, used only to update
                // the map
                .antMatchers("/css/**", "/js/**", "/images/**",
                        "/osprey.jpeg").permitAll()
                // Permits all request to static resources
                .antMatchers("/admin/**").hasAnyRole("ADMIN") // Only permits
                // authenticated users with the 'ADMIN' role to access the
                // admin panel
                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll()
                    .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }

    /**
     * Configures global authentication settings. Currently, this is being
     * used for demonstration purposes only. It needs to be configured to
     * perform password-hashing and fetch users from the database.
     * @param auth  SecurityBuilder used to create an AuthenticationManager,
     *              an interface used to process an Authentication request.
     * @throws Exception    If there is an error in authentication.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("admin").password("{noop}password").roles("ADMIN");
    }
}