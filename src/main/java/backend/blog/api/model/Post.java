//src/main/java/backend/blog/api/model/Post.java

package backend.blog.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import lombok.Data;

@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String postid;  // ← đổi từ _id / id
    /** Immutable owner id from JWT (used for authorization) */
    private String ownerId;
    private String title;
    private String desc;
    private String photo;   // store Cloudinary secure_url
    private String username;
    private List<String> categories;
    private String createdAt = java.time.Instant.now().toString(); // thêm createdAt
    private int commentCount = 0;  // Number of comments on this post
    private int likeCount = 0;      // Number of likes on this post
    private List<String> likedBy;   // List of user IDs who liked this post
}
