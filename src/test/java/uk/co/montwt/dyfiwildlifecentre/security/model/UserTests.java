/*
 * Copyright (C) 2020 Michael Male
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package uk.co.montwt.dyfiwildlifecentre.security.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class UserTests {

    private static jakarta.validation.Validator beanValidator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        beanValidator = factory.getValidator();
    }

    @Test
    public void validateSettersAndGetters() {
        PojoClass userPojo = PojoClassFactory.getPojoClass(User.class);
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();
        validator.validate(userPojo);
    }

    @Test
    public void validateBeanValidationConstraints() {
        User invalid = new User();
        invalid.setUsername("abc");
        invalid.setPassword("short");

        Set<ConstraintViolation<User>> violations = beanValidator.validate(invalid);
        Assertions.assertEquals(2, violations.size());

        User valid = new User();
        valid.setUsername("adminuser");
        valid.setPassword("secret1234");
        Assertions.assertTrue(beanValidator.validate(valid).isEmpty());
    }

    @Test
    public void validateJsonSerializationOmitsPasswordAndRoles() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("adminuser");
        user.setPassword("supersecret");
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        user.setRoles(Set.of(role));

        String json = new ObjectMapper().writeValueAsString(user);
        Assertions.assertTrue(json.contains("adminuser"));
        Assertions.assertFalse(json.contains("supersecret"));
        Assertions.assertFalse(json.contains("ROLE_ADMIN"));
    }
}
