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

package uk.co.montwt.dyfiwildlifecentre.security.model;

import javax.persistence.*;
import java.util.Set;

/**
 * Role.java - An object that represents a role. A role, or authority, is the
 * level of access that a user has, and is used by Spring Security to define
 * which parts of the app one can access.
 *
 * @author Michael Male
 * @version 1.0 2020-04-20
 */
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    /**
     * Gets the ID of the role
     * @return  Long containing ID of the role
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the role
     * @param id    Long containing ID of the role
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the role
     * @return  String containing the name of the role
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role
     * @param name  String containing the name of the role
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets users that are attached to the role
     * @return  Set of type User containing all User objects attached to the
     * role
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * Sets users that are attached to the role
     * @param users Set of type User containing all User objects attached to
     *              the role
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
