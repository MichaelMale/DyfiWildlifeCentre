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

package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MvcConfig.java - Implements a WebMvcConfigurer to provide a single method
 * that attaches view controllers to the possible entry points.
 *
 * @author Michael Male
 * @version 1.0 2020-04-12
 * @see WebMvcConfigurer
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Adds view controllers for the homepage, the login page, the admin
     * homepage, and a 403 error page.
     * @param registry  Object of type ViewControllerRegistry that assists
     *                  with the registration of automated controllers.
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin").setViewName("admin/home");
        registry.addViewController("/403").setViewName("/error/403");
    }
}
