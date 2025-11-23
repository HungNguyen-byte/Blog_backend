//src/main/java/backend/blog/api/service/JwtService.java

package backend.blog.api.service;

import backend.blog.api.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private SecretKey key() {
        if (secret == null || secret.length() < 32)
            throw new IllegalArgumentException("JWT secret too short");
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        var claims = Map.<String, Object>of("userid", user.getUserid());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public String extractUserId(String token) {
        return parse(token).get("userid", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> fn) {
        return fn.apply(parse(token));
    }

    public String getTokenFromHeader(String header) {
        if (header == null)
            return null;
        if (header.startsWith("Bearer "))
            return header.substring(7);
        return null;
    }

    // ...existing code...

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    public String getCurrentUserId(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("No token found in request");
        }
        return extractUserId(token);
    }
}
