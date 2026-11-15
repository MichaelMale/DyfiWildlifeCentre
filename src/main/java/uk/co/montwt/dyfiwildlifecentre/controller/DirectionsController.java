package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.montwt.dyfiwildlifecentre.service.DirectionsService;

import java.util.Map;

/**
 * DirectionsController.java - Exposes driving and public transport directions
 * from Dyfi Wildlife Centre to a visitor-supplied destination.
 */
@RestController
public class DirectionsController {

    private final DirectionsService directionsService;

    public DirectionsController(DirectionsService directionsService) {
        this.directionsService = directionsService;
    }

    @GetMapping("/directions")
    public DirectionsService.DirectionsResult getDirections(
            @RequestParam("destination") String destination,
            @RequestParam(value = "mode", defaultValue = "driving") String mode,
            @RequestParam(value = "direction", defaultValue = "fromCentre") String direction) {
        return directionsService.getDirections(destination, mode, direction);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadDestination(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}
