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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DyfiWildlifeCentreApplication {

    private static final Logger log = LoggerFactory.getLogger(DyfiWildlifeCentreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DyfiWildlifeCentreApplication.class, args);
    }

//    /**
//     * Adds some POI objects to a repository.
//     *
//     * TODO: Make this a debug flavour rather than the default one.
//     *
//     * @param repository    The repository that these objects are to be stored in.
//     * @return  Object of type CommandLineRunner to facilitate this method.
//     */
//    @Bean
//    public CommandLineRunner demo(PointOfInterestRepository repository) {
//
//        return (args) -> {
//            // Save some sample data
//            repository.save(new PointOfInterest("Place One", "Place One Description", 52.41806, -4.06576));
//            repository.save(new PointOfInterest("Place Two", "Place Two Description", 52.41806, -4.06576));
//            repository.save(new PointOfInterest("Place Three", "Place Three Description", 52.41806, -4.06576));
//            repository.save(new PointOfInterest("Place Four", "Place Four Description", 52.41806, -4.06576));
//
//            // Fetch all POIs
//            log.info("POIs found with findAll():");
//            for (PointOfInterest pointOfInterest : repository.findAll()) {
//                log.info(pointOfInterest.toString());
//            }
//            log.info("");
//
//            // Fetch an individual POI by ID
//            PointOfInterest pointOfInterest = repository.findById(1);
//            log.info("POI found with findById(1):");
//            log.info(pointOfInterest.toString());
//            log.info("");
//
//            // Fetch POI by name
//            log.info("POI(s) found with findByName(\"Place One\"):");
//            repository.findByName("Place One").forEach(placeOne -> {
//                log.info(placeOne.toString());
//            });
//        };
//
//    }

}
