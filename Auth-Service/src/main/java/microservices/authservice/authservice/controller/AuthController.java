package microservices.authservice.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.authservice.authservice.entity.User;
import microservices.authservice.authservice.model.*;
import microservices.authservice.authservice.service.RefreshTokenService;
import microservices.authservice.authservice.service.UserService;
import microservices.authservice.authservice.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        try {
            String token = request.getToken();
            
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.ok(new TokenValidationResponse(false, "Token is required"));
            }

            // Remove Bearer prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String email = jwtUtil.getEmailFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if (jwtUtil.validateToken(token, email)) {
                return ResponseEntity.ok(new TokenValidationResponse(true, userId, email, role));
            } else {
                return ResponseEntity.ok(new TokenValidationResponse(false, "Invalid or expired token"));
            }
        } catch (Exception e) {
            log.error("Error validating token", e);
            return ResponseEntity.ok(new TokenValidationResponse(false, "Invalid token format"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenValidationRequest request) {
        try {
            String refreshToken = request.getToken();

            if (refreshToken == null || !refreshTokenService.validateRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest().build();
            }

            Long userId = refreshTokenService.getUserIdFromRefreshToken(refreshToken);
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }

            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = userOpt.get();
            String newAccessToken = jwtUtil.generateToken(user);
            String newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

            // Delete old refresh token
            refreshTokenService.deleteRefreshToken(refreshToken);

            AuthResponse response = new AuthResponse(newAccessToken, newRefreshToken, UserInfo.fromUser(user));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error refreshing token", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String email = jwtUtil.getEmailFromToken(token);

            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(UserInfo.fromUser(userOpt.get()));
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);

            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<User> users = userService.findAllUsers();
            List<UserInfo> userInfos = users.stream()
                    .map(UserInfo::fromUser)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userInfos);
        } catch (Exception e) {
            log.error("Error getting all users", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }

    @PostMapping("/test-token")
    public ResponseEntity<AuthResponse> createTestToken() {
        // Tạo test user để demo
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setGithubUsername("testuser");
        testUser.setRole(User.Role.USER);
        testUser.setIsActive(true);

        String accessToken = jwtUtil.generateToken(testUser);
        String refreshToken = refreshTokenService.createRefreshToken(testUser.getId());

        AuthResponse response = new AuthResponse(accessToken, refreshToken, UserInfo.fromUser(testUser));
        return ResponseEntity.ok(response);
    }
}
