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

package uk.co.montwt.dyfiwildlifecentre.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

    @Query("SELECT poi FROM PointOfInterest poi WHERE LOWER(poi.name) = LOWER(:name)")
    List<PointOfInterest> findAllPointsOfInterestByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE PointOfInterest SET name =(:name), description=(:description), latitude=(:latitude), longitude=" +
            "(:longitude) " +
            "WHERE id=(:id)")
    void update(@Param("name") String name,
                @Param("description") String description,
                @Param("latitude") double latitude,
                @Param("longitude") double longitude,
                @Param("id") long id);
}