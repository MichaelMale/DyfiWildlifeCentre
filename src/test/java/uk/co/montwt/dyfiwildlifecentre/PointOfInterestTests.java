package uk.co.montwt.dyfiwildlifecentre;

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
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;

import java.awt.geom.Point2D;

@SpringBootTest
public class PointOfInterestTests {

    private static PointOfInterest poi;

    @BeforeAll
    static void init() {
        poi = new PointOfInterest(
                "Aberystwyth University",
                "Aberystwyth University (Welsh: Prifysgol Aberystwyth) is a public research university in " +
                        "Aberystwyth, Wales. Aberystwyth was a founding member institution of the former federal" +
                        " University of Wales. The university has over 8,000 students studying across 3 academic" +
                        " faculties and 17 departments.",
                52.41806,
                -4.06576
        );
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
        Assertions.assertEquals(new Point2D.Double(52.41806, -4.06576), poi.generateCoordinates());
    }

    @Test
    @DisplayName("Confirm that a valid JSON representation is returned when calling method.")
    public void whenJSONMethodIsCalled_MethodReturnsValidRepresentation() {
        Assertions.assertEquals("{\"name\":\"Aberystwyth University\",\"description\":\"Aberystwyth University " +
                        "(Welsh: Prifysgol Aberystwyth) is a public research university in Aberystwyth, Wales. " +
                        "Aberystwyth was a founding member institution of the former federal University of Wales." +
                        " The university has over 8,000 students studying across 3 academic faculties and 17" +
                        " departments.\",\"latitude\":52.41806,\"longitude\":-4.06576}",
                poi.toJSON());
    }

    @Test
    @DisplayName("Confirm that the equals method returns true if objects are the same.")
    public void whenEqualsMethodIsCalledOnEqualObjects_MethodReturnsTrue() {
        PointOfInterest secondPOI = new PointOfInterest(
                "Aberystwyth University",
                "Aberystwyth University (Welsh: Prifysgol Aberystwyth) is a public research university in " +
                        "Aberystwyth, Wales. Aberystwyth was a founding member institution of the former federal" +
                        " University of Wales. The university has over 8,000 students studying across 3 academic" +
                        " faculties and 17 departments.",
                52.41806,
                -4.06576
        );
        Assertions.assertTrue(poi.equals(secondPOI));
    }

    @Test
    @DisplayName("Confrim that the equals method returns false if objects are not the same.")
    public void whenEqualsMethodIsCalledOnUnequalObjects_MethodReturnsFalse() {
        PointOfInterest unequalPOI = new PointOfInterest("Title", "Description", 0, 0);
        Assertions.assertFalse(poi.equals(unequalPOI));
    }

}
