//src/main/java/backend/blog/api/controller/AuthController.java

package backend.blog.api.controller;
import backend.blog.api.dto.AuthResponse;
import backend.blog.api.dto.LoginRequest;
import backend.blog.api.dto.RegisterRequest;
import backend.blog.api.model.User;
import backend.blog.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User u = new User();
        u.setUsername(req.getUsername()); u.setEmail(req.getEmail()); u.setPassword(req.getPassword());
        User saved = authService.register(u);
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }
}
