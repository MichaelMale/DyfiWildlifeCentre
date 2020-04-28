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

package uk.co.montwt.dyfiwildlifecentre.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;

import javax.transaction.Transactional;
import java.util.List;

/**
 * PointOfInterestRepository.java - An interface used to extend a repository
 * and connect to the SQL database.
 *
 * @author Michael Male
 * @version 1.0 2020-04-12
 * @see JpaRepository
 */
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

    /**
     * A custom query, used to find all points of interest where the given
     * name is the same as a name in the database.
     * @param name  The name to be checked.
     * @return  List containing all Points Of Interest with that name. This
     * is a one-to-many operation as there could be multiple POIs with the
     * same name.
     */
    @Query("SELECT poi FROM PointOfInterest poi WHERE LOWER(poi.name) = LOWER(:name)")
    List<PointOfInterest> findAllPointsOfInterestByName(String name);

}