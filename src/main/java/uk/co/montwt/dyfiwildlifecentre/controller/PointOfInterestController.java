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

package uk.co.montwt.dyfiwildlifecentre.controller;


import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.service.ImageStorageService;
import uk.co.montwt.dyfiwildlifecentre.service.PointOfInterestServiceImpl;

import java.io.IOException;
import java.util.List;

/**
 * PointOfInterestController.java - A controller that implements a RESTful
 * API for points of interest, including CRUD methods.
 *
 * @author Michael Male
 * @version 0.1 2020-04-12
 * @see RestController
 */
@RestController
public class PointOfInterestController{

    private final PointOfInterestServiceImpl pointOfInterestService;
    private final ImageStorageService imageStorageService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PointOfInterestController(PointOfInterestServiceImpl pointOfInterestService,
                                     ImageStorageService imageStorageService) {
        this.pointOfInterestService = pointOfInterestService;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Gets a POI by its ID.
     *
     * @param id ID of the POI.
     * @return POI that relates to this ID, null if not found.
     */
    @GetMapping("/poi/get/id/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public PointOfInterest getPointOfInterestById(@PathVariable("id") long id) {
        return pointOfInterestService.findById(id);
    }

    /**
     * Gets all Points of Interest in the DAO.
     *
     * @return List of type PointOfInterest containing all POIs received from the DAO.
     */
    @GetMapping("/poi")
    public List<PointOfInterest> getAllPointsOfInterest() {
        return pointOfInterestService.findAll();
    }

    /**
     * Submits a Point of Interest based on information in the form.
     *
     * @param pointOfInterest A POI
     * @return RedirectView which redirects the server to the index page, which will contain the newly created point
     * of interest.
     */
    @PostMapping("/poi/form_create")
    public View submitPointOfInterest(@ModelAttribute PointOfInterest pointOfInterest,
                                      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                      RedirectAttributes redirectAttributes)
            throws IOException {
        if (pointOfInterest.getStationCode() != null && pointOfInterest.getStationCode().isBlank()) {
            pointOfInterest.setStationCode(null);
        }
        if (pointOfInterest.getBusStopCode() != null && pointOfInterest.getBusStopCode().isBlank()) {
            pointOfInterest.setBusStopCode(null);
        }
        applyUploadedImage(pointOfInterest, imageFile);
        pointOfInterestService.save(pointOfInterest);
        logger.info("POI saved: \n" + pointOfInterest.toString());
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.poiCreated");
        redirectAttributes.addFlashAttribute("flashSuccessArg", pointOfInterest.getName());
        return new RedirectView("/index");
    }

    /**
     * Gets a Point of Interest by its name.
     *
     * @param name Name of the Point of Interest.
     * @return POI if found, null if not.
     */
    @GetMapping("poi/get/name/{name}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<PointOfInterest> getPointOfInterestsByName(@PathVariable("name") String name) {
        return pointOfInterestService.findAllPointsOfInterestByName(name);
    }

    /**
     * Deletes a Point of Interest by its ID.
     *
     * @param id ID pertaining to the Point of Interest.
     * @return POI that was deleted.
     */
    @PostMapping("/poi/delete")
    public RedirectView deletePointOfInterestById(@RequestParam("id") long id,
                                                  RedirectAttributes redirectAttributes) {
        pointOfInterestService.delete(id);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.poiDeleted");
        return new RedirectView("/admin/list");
    }

    /**
     * Updates a Point of Interest, given its ID.
     *
     * @param poi   The Point Of Interest with its updated members.
     * @param id    The Point of Interest's ID
     * @return  A redirect that indicates the outcome of the method.
     */
    @PostMapping("/poi/update")
    public RedirectView updatePointOfInterest(@ModelAttribute PointOfInterest poi,
                                              @RequestParam("id") long id,
                                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                              RedirectAttributes redirectAttributes)
            throws IOException {
        poi.setId(id);
        if (poi.getStationCode() != null && poi.getStationCode().isBlank()) {
            poi.setStationCode(null);
        }
        if (poi.getBusStopCode() != null && poi.getBusStopCode().isBlank()) {
            poi.setBusStopCode(null);
        }
        applyUploadedImage(poi, imageFile);
        pointOfInterestService.update(poi);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.poiUpdated");
        redirectAttributes.addFlashAttribute("flashSuccessArg", poi.getName());
        return new RedirectView("/");
    }

    private void applyUploadedImage(PointOfInterest poi, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            poi.setImageUrl(imageStorageService.storeImage(imageFile));
        } else if (poi.getImageUrl() != null && poi.getImageUrl().isBlank()) {
            poi.setImageUrl(null);
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({PostcodeException.class, IllegalArgumentException.class})
    public ModelAndView invalidPostcode(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex.getMessage());
        mav.setViewName("error/400");
        return mav;
    }

    /**
     * Handles a data integrity violation.
     * @param ex    Exception that is thrown when there is a data integrity
     *              violation.
     * @return  Model containing an error message and view containing an
     * Error 400 template.
     */
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView dataIntegrityViolation(DataIntegrityViolationException ex) {
        ModelAndView mav = new ModelAndView();
        String nameOfViolatedConstraint = null;
        if (ex.getCause() instanceof ConstraintViolationException) {
            nameOfViolatedConstraint =
                    ((ConstraintViolationException) ex.getCause()).getConstraintName();
        }
        String errorMessage;
        // Checks if a localised error message can be provided, based upon
        // the violated constraint.
        if ("points_of_interest_latitude_longitude_key".equals(nameOfViolatedConstraint)) {
            errorMessage = "There is already a POI with this location.";
        } else if ("latitude_not_null_island_chk".equals(nameOfViolatedConstraint)
                || "longitude_not_null_island_chk".equals(nameOfViolatedConstraint)) {
            errorMessage = "No location or an invalid location has been " +
                    "entered, please try again";
        } else {
            errorMessage = ex.getMostSpecificCause().getMessage();
        }
        mav.addObject("exception", errorMessage);
        mav.setViewName("error/400");
        return mav;
    }


}
