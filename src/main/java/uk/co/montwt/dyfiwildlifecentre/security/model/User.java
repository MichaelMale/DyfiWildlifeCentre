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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * User.java - An object that represents a user. A user in this context is
 * someone who can access an authenticated area and manage the web
 * application from its content management system.
 *
 * @author Michael Male
 * @version 0.1 2020-04-20
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Error: A username must be provided.")
    @Column(unique = true)
    @Size(min = 6, max = 32, message = "Error: Username must be between 6 and" +
            " 32 characters.")
    private String username;

    @NotEmpty(message = "Error: A password must be provided.")
    @Size(min = 8, message = "Error: Password must be at least 8 characters.")
    private String password;

    @ManyToMany
    private Set<Role> roles;

    /**
     * Gets the ID of the user
     * @return  Long containing the ID of the User
     */
    public Long getId() { return id; }

    /**
     * Gets the username of the user
     * @return  String containing the username of the User
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user
     * @param username  String containing the username of the User
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user, it is not included when this object is
     * serialised as a JSON string
     * @return  String containing the password
     */
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password
     * @param password  String containing the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets roles attached to the user, it is not included when this object
     * is serialised as a JSON string
     * @return  Set of type Role containing all roles attached to the User
     */
    @JsonIgnore
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets roles attached to the user.
     * @param roles Set of type Role containing all roles to be attached to
     *              the user
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
