package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.montwt.dyfiwildlifecentre.model.WildlifeSighting;
import uk.co.montwt.dyfiwildlifecentre.service.WildlifeSightingService;

import java.util.List;

@RestController
public class SightingController {

    private final WildlifeSightingService wildlifeSightingService;

    public SightingController(WildlifeSightingService wildlifeSightingService) {
        this.wildlifeSightingService = wildlifeSightingService;
    }

    @GetMapping("/sightings")
    public List<WildlifeSighting> getLatestSightings() {
        return wildlifeSightingService.findLatestPublic();
    }
}
