// src/main/java/backend/blog/api/controller/UploadController.java
package backend.blog.api.controller;

import backend.blog.api.service.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UploadController {

    private final ImageUploadService imageUploadService;

    public UploadController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = imageUploadService.uploadImage(file);

            return ResponseEntity.ok(
                java.util.Map.of(
                    "message", "Upload success",
                    "url", url
                )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of(
                    "error", "Upload failed",
                    "details", e.getMessage()
                )
            );
        }
    }
}
