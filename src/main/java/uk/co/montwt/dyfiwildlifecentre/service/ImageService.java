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

package uk.co.montwt.dyfiwildlifecentre.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.co.montwt.dyfiwildlifecentre.model.Image;
import uk.co.montwt.dyfiwildlifecentre.model.repository.ImageRepository;

import javax.imageio.ImageIO;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image storeImage(MultipartFile file) {

        // Normalise file name
        String fileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Check if invalid characters

            if (fileName.contains("..")) {
                throw new FileStorageException("File name contains invalid " +
                        "path sequence " + fileName);
            } else if (ImageIO.read(file.getInputStream()) == null) {
                throw new FileStorageException("File is not an image");
            }

            Image image = new Image(fileName, file.getBytes());

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new FileStorageExcpetion("Error while storing " + fileName,
                    e);
        }
    }

    public Image getImage(String imageId) throws FileNotFoundException {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new FileNotFoundException("File not found " +
                        "with id " + imageId));
    }

}
