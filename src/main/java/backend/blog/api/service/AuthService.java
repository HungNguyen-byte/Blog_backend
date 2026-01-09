//src/main/java/backend/blog/api/service/AuthService.java

package backend.blog.api.service;

import backend.blog.api.dto.AuthResponse;
import backend.blog.api.dto.LoginRequest;
import backend.blog.api.model.User;
import backend.blog.api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** ------------------------------
     *  Register new user
     * ------------------------------ */
    public User register(User newUser) {

        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Encode password
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // profilePic → dùng default empty string nếu null
        if (newUser.getProfilePic() == null) {
            newUser.setProfilePic("");
        }

        // bio → dùng default empty string nếu null
        if (newUser.getBio() == null) {
            newUser.setBio("");
        }

        // Ensure admin is false for new registrations (security: prevent privilege escalation)
        newUser.setAdmin(false);

        User saved = userRepository.save(newUser);

        // Không bao giờ trả password về client
        saved.setPassword(null);

        return saved;
    }

    /** ------------------------------
     *  Login existing user
     * ------------------------------ */
    public AuthResponse login(LoginRequest req) {

        // Will throw exception automatically if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove password before sending back
        user.setPassword(null);

        // Generate JWT
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user);
    }
}
