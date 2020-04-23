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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * WebSecurityConfig.java - This class manages the instance of the Spring
 * Security plugin that is being used for this application. It provides
 * various methods that call beans and manage which parts of the website can
 * be seen by visitors, and which are authenticated.
 *
 * @author Michael Male
 * @version 0.1 2020-04-20
 * @see WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;

    /**
     * Constructor for objects of type WebSecurityConfig. Autowired to
     * include a UserDetailsService
     * @param userDetailsService Autowired class
     * @see uk.co.montwt.dyfiwildlifecentre.security.service.UserDetailsServiceImpl
     */
    @Autowired
    public WebSecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Calls a bean to instantiate a BCryptPassword encoder, that hashes
     * passwords utilising the BCrypt hashing algorithm.
     * @return  An instance of BCryptPasswordEncoder.
     * @see BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security parameters for the application. Further
     * details are included within inline comments in the source code.
     *
     * @param http  An instance of HttpSecurity
     * @throws Exception    if there is an issue with the security
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
                .antMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // Permits all request to static resources
                .antMatchers("/admin/**").hasRole("ADMIN") // Only
                // permits
                // authenticated users with the 'ADMIN' role to access the
                // admin panel
                .anyRequest().authenticated()
                .and()
                .formLogin()// Sets authenticated requests to be routed via
                // the login page
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    /**
     * Calls a bean to instantiate an AuthenticationManager.
     * @return  Object of type AuthenticationManager
     * @throws Exception    If there is an issue with the authentication.
     */
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    /**
     * Configures the user details service by adding the BCrypt encoder to it
     * . This is then used to hash the password that is eventually saved into
     * the database.
     * @param auth  Object of type AuthenticationManagerBuilder that builds
     *              an object of type AuthenticationManager.
     * @throws Exception    if there is an issue with authentication or
     * configuration.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }



}
