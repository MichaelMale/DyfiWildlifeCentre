package uk.co.montwt.dyfiwildlifecentre.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

public class ImageStorageServiceTests {

    @TempDir
    Path tempDir;

    @Test
    void storesValidImageAndReturnsUploadsPath() throws Exception {
        ImageStorageService service = new ImageStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "imageFile", "osprey.jpg", "image/jpeg", "fake-image-bytes".getBytes());

        String webPath = service.storeImage(file);

        Assertions.assertTrue(webPath.startsWith("/uploads/"));
        Assertions.assertTrue(webPath.endsWith(".jpg"));
        String filename = webPath.substring("/uploads/".length());
        Assertions.assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void rejectsEmptyOrUnsupportedFiles() {
        ImageStorageService service = new ImageStorageService(tempDir.toString());
        MockMultipartFile empty = new MockMultipartFile("imageFile", "empty.png", "image/png", new byte[0]);
        MockMultipartFile script = new MockMultipartFile("imageFile", "evil.js", "application/javascript", "alert(1)".getBytes());

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.storeImage(empty));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.storeImage(script));
    }
}
