package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.montwt.dyfiwildlifecentre.config.WebcamProperties;
import uk.co.montwt.dyfiwildlifecentre.service.WebcamService;

import java.util.List;

/**
 * WebcamController.java - Exposes configured reserve webcam feeds for the map viewer.
 */
@RestController
public class WebcamController {

    private final WebcamService webcamService;

    public WebcamController(WebcamService webcamService) {
        this.webcamService = webcamService;
    }

    @GetMapping("/webcams")
    public List<WebcamProperties.WebcamFeed> getWebcams() {
        return webcamService.getActivePublicFeeds();
    }
}
