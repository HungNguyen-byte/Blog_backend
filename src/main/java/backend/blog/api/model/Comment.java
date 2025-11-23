//src/main/java/backend/blog/api/model/Comment.java

package backend.blog.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "comments")
public class Comment {
    @Id
    private String commentid;
    private String postid;      // Post this comment belongs to
    private String username;   // User who made the comment
    private String text;        // Comment content
    private String createdAt = java.time.Instant.now().toString();
}

