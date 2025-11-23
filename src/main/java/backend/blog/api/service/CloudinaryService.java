//src/main/java/backend/blog/api/service/CloudinaryServive.java

package backend.blog.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    public CloudinaryService(Cloudinary cloudinary) { this.cloudinary = cloudinary; }

    public Map upload(byte[] bytes, String publicId) throws IOException {
        return cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "auto"
        ));
    }
}
