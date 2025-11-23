// src/main/java/backend/blog/api/service/UserService.java
package backend.blog.api.service;

import backend.blog.api.model.User;
import backend.blog.api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    private String resolveCurrentUserId(HttpServletRequest request) {
        try {
            if (request != null) {
                String idFromToken = jwtService.getCurrentUserId(request);
                if (idFromToken != null && !idFromToken.isBlank()) {
                    return idFromToken;
                }
            }
        } catch (Exception ignored) {
            // nếu token không có hoặc parse lỗi thì fallback xuống SecurityContext
        }

        // 2) Fallback: từ SecurityContext (principal có thể là UserDetails hoặc username String)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
                // nếu UserDetails chứa id riêng, cần cast và lấy; nếu không có thì trả username
                UserDetails ud = (UserDetails) principal;
                // trả username làm fallback (frontend/back-end phải đồng bộ)
                return ud.getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }

        // không tìm được
        throw new RuntimeException("Unable to resolve current user id");
    }

    public User updateUser(String id, User update, HttpServletRequest request) {
        // Check ownership: id param phải trùng id trong token (hoặc SecurityContext)
        String currentUserId = resolveCurrentUserId(request);

        if (!currentUserId.equals(id)) {
            throw new RuntimeException("You can only update your own account");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // === UPDATE FIELDS ===
        if (update.getUsername() != null && !update.getUsername().isBlank()) {
            user.setUsername(update.getUsername());
        }

        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            user.setEmail(update.getEmail());
        }

        if (update.getProfilePic() != null &&
                !update.getProfilePic().isBlank() &&
                !update.getProfilePic().equals(user.getProfilePic())) {

            user.setProfilePic(update.getProfilePic());
        }

        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            user.setPassword(encoder.encode(update.getPassword()));
        }

        User saved = userRepository.save(user);
        saved.setPassword(null);
        return saved;
    }

    public void deleteUser(String id, HttpServletRequest request) {
        String currentUserId = resolveCurrentUserId(request);

        if (!currentUserId.equals(id)) {
            throw new RuntimeException("You can only delete your own account");
        }

        userRepository.deleteById(id);
    }

    public User getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(null);
        return user;
    }
}
