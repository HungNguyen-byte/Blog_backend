// src/main/java/backend/blog/api/model/Category

package backend.blog.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "categories")
public class Category {

    @Id
    private String categoryid;

    @NotBlank
    private String name;

    private int postCount = 0;
    
    // Track when this category was last used in a post
    private Instant lastUsedDate = Instant.now();
}
