/* backend/blog/api/dto/RegisterRequest.java */
package backend.blog.api.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
