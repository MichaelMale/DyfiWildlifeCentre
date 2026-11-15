package uk.co.montwt.dyfiwildlifecentre.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * ImageStorageService.java - Handles safe local storage of uploaded POI images.
 */
@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final Path uploadRoot;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * Validates and stores an uploaded image file, returning its public {@code /uploads/...} URL path.
     *
     * @param file Uploaded multipart file.
     * @return Relative web URL to the stored file.
     * @throws IOException If writing the file fails.
     * @throws IllegalArgumentException If the file type is unsupported or missing.
     */
    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded image file is empty");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String extension = extractAllowedExtension(file.getOriginalFilename(), contentType);
        Files.createDirectories(uploadRoot);

        String storedFilename = UUID.randomUUID() + "." + extension;
        Path target = uploadRoot.resolve(storedFilename).normalize();
        if (!target.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid upload path");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return "/uploads/" + storedFilename;
    }

    private static String extractAllowedExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0 && dot < originalFilename.length() - 1) {
                String ext = originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT);
                if (ALLOWED_EXTENSIONS.contains(ext)) {
                    return ext;
                }
            }
        }
        if (contentType != null) {
            String lower = contentType.toLowerCase(Locale.ROOT);
            if (lower.contains("png")) return "png";
            if (lower.contains("jpeg") || lower.contains("jpg")) return "jpg";
            if (lower.contains("gif")) return "gif";
            if (lower.contains("webp")) return "webp";
        }
        throw new IllegalArgumentException("Unsupported image format. Allowed: png, jpg, jpeg, gif, webp");
    }
}
