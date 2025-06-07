package microservices.productservice.productservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String userId = request.getHeader(USER_ID_HEADER);
        String userEmail = request.getHeader(USER_EMAIL_HEADER);
        String userRole = request.getHeader(USER_ROLE_HEADER);

        // If headers are present, it means the request came through Gateway and is authenticated
        if (userId != null && userEmail != null && userRole != null) {
            log.debug("Authenticated user from Gateway - ID: {}, Email: {}, Role: {}", userId, userEmail, userRole);
            
            // Create UserPrincipal with user information
            UserPrincipal userPrincipal = new UserPrincipal(
                Long.parseLong(userId), 
                userEmail, 
                userRole
            );
            
            // Create authentication token with role as authority
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userPrincipal, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority(userRole))
                );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.debug("No user headers found - request may not be from Gateway or user not authenticated");
        }

        filterChain.doFilter(request, response);
    }

    // User principal class to hold user information
    public static class UserPrincipal {
        private final Long userId;
        private final String email;
        private final String role;

        public UserPrincipal(Long userId, String email, String role) {
            this.userId = userId;
            this.email = email;
            this.role = role;
        }

        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getRole() { return role; }

        @Override
        public String toString() {
            return "UserPrincipal{" +
                    "userId=" + userId +
                    ", email='" + email + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}
