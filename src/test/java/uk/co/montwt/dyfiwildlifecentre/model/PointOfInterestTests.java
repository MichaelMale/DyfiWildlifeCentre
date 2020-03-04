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

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class PointOfInterestTests {

    private static List<PointOfInterest> poiList;

    @BeforeAll
    static void init() {
        poiList = new ArrayList<>();
        PointOfInterest poi = new PointOfInterest(
                "Aberystwyth University",
                "Aberystwyth University (Welsh: Prifysgol Aberystwyth) is a public research university in " +
                        "Aberystwyth, Wales. Aberystwyth was a founding member institution of the former federal" +
                        " University of Wales. The university has over 8,000 students studying across 3 academic" +
                        " faculties and 17 departments.",
                52.41806,
                -4.06576
        );
        PointOfInterest secondPOI = new PointOfInterest(
                "Aberystwyth University",
                "Aberystwyth University (Welsh: Prifysgol Aberystwyth) is a public research university in " +
                        "Aberystwyth, Wales. Aberystwyth was a founding member institution of the former federal" +
                        " University of Wales. The university has over 8,000 students studying across 3 academic" +
                        " faculties and 17 departments.",
                52.41806,
                -4.06576
        );
        PointOfInterest unequalPOI = new PointOfInterest("Title", "Description", 0, 0);
        poiList.add(poi);
        poiList.add(secondPOI);
        poiList.add(unequalPOI);
    }

    @Test
    @DisplayName("All setters and getters should be able to set and return a given value.")
    public void validateSettersAndGetters() {
        PojoClass pointOfInterestPojo = PojoClassFactory.getPojoClass(PointOfInterest.class);

        Validator validator = ValidatorBuilder
                .create()
                .with(new SetterMustExistRule())
                .with(new GetterMustExistRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(pointOfInterestPojo);
    }

    @Test
    @DisplayName("A Point of Interest with a latitude and longitude passed to it should be able to return a Point2D " +
            "object containing both values.")
    public void whenPOIIsInstantiated_MethodCanReturnAPoint2DRepresentation() {
        Assertions.assertEquals(new Point2D.Double(52.41806, -4.06576), poiList.get(0).generateCoordinates());
    }

    @Test
    @DisplayName("Confirm that a valid JSON representation is returned when calling method.")
    public void whenJSONMethodIsCalled_MethodReturnsValidRepresentation() {
        Assertions.assertEquals("{\"id\":0,\"name\":\"Aberystwyth University\",\"description\":\"Aberystwyth " +
                        "University " +
                        "(Welsh: Prifysgol Aberystwyth) is a public research university in Aberystwyth, Wales. " +
                        "Aberystwyth was a founding member institution of the former federal University of Wales." +
                        " The university has over 8,000 students studying across 3 academic faculties and 17" +
                        " departments.\",\"latitude\":52.41806,\"longitude\":-4.06576}",
                poiList.get(0).toJSON());
    }
}
