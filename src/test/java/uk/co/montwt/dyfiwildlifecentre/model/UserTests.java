///*
// * Copyright (C) 2020 Michael Male
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *  http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//package uk.co.montwt.dyfiwildlifecentre.model;
//
//import com.openpojo.reflection.PojoClass;
//import com.openpojo.reflection.impl.PojoClassFactory;
//import com.openpojo.validation.Validator;
//import com.openpojo.validation.ValidatorBuilder;
//import com.openpojo.validation.rule.impl.GetterMustExistRule;
//import com.openpojo.validation.rule.impl.SetterMustExistRule;
//import com.openpojo.validation.test.impl.GetterTester;
//import com.openpojo.validation.test.impl.SetterTester;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class UserTests {
//
////    private static User testUser;
////
////    @BeforeAll
////    static void init() {
////        testUser = new User("test", "test");
////    }
//
//    @Test
//    @DisplayName("All setters and getters should be able to set and return a " +
//            "given value.")
//    public void validateSettersAndGetters() {
//        PojoClass userPojo =
//                PojoClassFactory.getPojoClass(User.class);
//
//        Validator validator = ValidatorBuilder
//                .create()
//                .with(new SetterMustExistRule())
//                .with(new GetterMustExistRule())
//                .with(new SetterTester())
//                .with(new GetterTester())
//                .build();
//
//        validator.validate(userPojo);
//    }
//}