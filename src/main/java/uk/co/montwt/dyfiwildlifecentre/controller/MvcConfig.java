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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

/**
 * MvcConfig.java - Implements a WebMvcConfigurer to provide a single method
 * that attaches view controllers to the possible entry points and configures
 * internationalisation (English / Welsh).
 *
 * @author Michael Male
 * @version 1.1 2026-11-14
 * @see WebMvcConfigurer
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Resolves user locale from a cookie, defaulting to English.
     *
     * @return Configured {@link LocaleResolver}.
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("dyfi_lang");
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieMaxAge(Duration.ofDays(30));
        return resolver;
    }

    /**
     * Intercepts {@code ?lang=en} / {@code ?lang=cy} query parameters to switch locale.
     *
     * @return Configured {@link LocaleChangeInterceptor}.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response,
                                   Object handler, ModelAndView modelAndView) {
                if (modelAndView != null && "index".equals(modelAndView.getViewName())) {
                    modelAndView.addObject("kioskMode", "true".equalsIgnoreCase(request.getParameter("kiosk")));
                }
            }
        });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadLocation = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation.endsWith("/") ? uploadLocation : uploadLocation + "/");
    }

    /**
     * Adds view controllers for the homepage, the login page, the admin
     * homepage, and a 403 error page.
     * @param registry  Object of type ViewControllerRegistry that assists
     *                  with the registration of automated controllers.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin").setViewName("admin/home");
        registry.addViewController("/403").setViewName("error/403");
    }
}
