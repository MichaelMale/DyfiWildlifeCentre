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

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfig.java - This class manages the instance of the Spring
 * Security plugin that is being used for this application. It provides
 * various methods that call beans and manage which parts of the website can
 * be seen by visitors, and which are authenticated.
 *
 * @author Michael Male
 * @version 0.2 2026-11-14
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

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
     * @return      The configured SecurityFilterChain
     * @throws Exception    if there is an issue with the security
     * configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/", "/index", "/poi", "/directions", "/webcams", "/sightings", "/error", "/403").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/videos/**", "/data/**", "/uploads/**").permitAll()
                        .requestMatchers("/admin/**", "/poi/form_create", "/poi/update", "/poi/delete", "/poi/get/**", "/users", "/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );
        return http.build();
    }

    /**
     * Calls a bean to instantiate an AuthenticationManager.
     * @param authenticationConfiguration Spring Security's AuthenticationConfiguration
     * @return  Object of type AuthenticationManager
     * @throws Exception    If there is an issue with the authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
