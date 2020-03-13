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

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterestRepository;

@Configuration
public class LoadDatabase {

    @Bean
    public CommandLineRunner demo(PointOfInterestRepository repository) {

        return (args) -> {
            // Save some sample data
            repository.save(new PointOfInterest("Dyfi Wildlife Centre", "Place One Description", 52.568774, -3.918031));
            repository.save(new PointOfInterest("Aberystwyth University", "Place Two Description", 52.4125057,
                    -4.06108));
            repository.save(new PointOfInterest("Glyndale", "Place Three Description", 52.4162989, -4.07767));
            repository.save(new PointOfInterest("Poole Harbour", "Place Four Description", 50.6937948, -2.0061704));
        };
    }
}
