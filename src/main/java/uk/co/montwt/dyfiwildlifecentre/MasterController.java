package uk.co.montwt.dyfiwildlifecentre;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.montwt.dyfiwildlifecentre.model.POIRepository;

import java.util.Map;

@Controller
public class MasterController {

    private POIRepository repository = new POIRepository();

    @RequestMapping("/")
    public ModelAndView thymeleafView(Map<String, Object> model) {
        model.put("marker_test_name", repository.getPointsOfInterest().get(1).getName());
        model.put("marker_test_long", repository.getPointsOfInterest().get(1).getLongitude());
        model.put("marker_test_lat", repository.getPointsOfInterest().get(1).getLatitude());
        return new ModelAndView("index");
    }

}
