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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.montwt.dyfiwildlifecentre.model.Image;
import uk.co.montwt.dyfiwildlifecentre.service.ImageService;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@RestController
public class ImageController {

    private static final Logger logger =
            LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String imageId)
            throws FileNotFoundException {

        Image image = imageService.getImage(imageId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getImage().getTy))
    }

}
