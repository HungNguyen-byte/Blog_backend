/* api/dto/AuthResponse.java */

package backend.blog.api.dto;

import backend.blog.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthResponse {
    private String token;
    private User user;
}
