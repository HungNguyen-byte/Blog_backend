/* api/dto/LoginRequest.java */

package backend.blog.api.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}