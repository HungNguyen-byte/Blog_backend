/* api/model/User.java */

package backend.blog.api.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String userid;
    private String username;
    private String email;
    private String password;
    private String profilePic;
    private String bio;
    private Instant createdAt;
    @JsonProperty("isAdmin")
    @Builder.Default
    private boolean admin = false; // Default to false for all new users
}