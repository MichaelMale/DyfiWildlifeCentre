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

package uk.co.montwt.dyfiwildlifecentre.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uk.co.montwt.dyfiwildlifecentre.security.model.RoleRepository;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.model.UserRepository;

import java.util.HashSet;
import java.util.List;

/**
 * UserService.java - A class to manage the user service.
 *
 * @author Michael Male
 * @version 0.2 2020-04-24
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Constructor for objects of type UserService
     * @param userRepository    Autowired object of type UserRepository
     * @param roleRepository    Autowired object of type RoleRepository
     * @param bCryptPasswordEncoder Autowired object of type
     *                              BCryptPasswordEncoder
     */
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Saves a User to the database.
     * @param user  User to be saved to the database
     */
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // Encodes the password using the BCrypt hashing algorithm
        user.setRoles(new HashSet<>(roleRepository.findAll())); // Sets role
        // to all roles available in the role repository. The current
        // implementation allows for one role, ROLE_ADMIN, as general users
        // don't need to be authenticated
        userRepository.save(user);
    }

    /**
     * Gets all users in the database, sorted in an ascending direction by
     * username
     * @return  List of type User, containing all users in the database
     */
    public List<User> getAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC,
                "username"));
    }

    /**
     * Deletes a user from the database
     * @param id    Long containing the ID of the user to be deleted
     */
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

}
