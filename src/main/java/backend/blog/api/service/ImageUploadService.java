// src/main/java/backend/blog/api/service/ImageUploadService.java
package backend.blog.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        // create random public_id
        String publicId = UUID.randomUUID().toString();

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "blog_uploads",
                        "public_id", publicId,
                        "overwrite", true,
                        "resource_type", "image"
                )
        );

        return (String) uploadResult.get("secure_url");
    }
}
