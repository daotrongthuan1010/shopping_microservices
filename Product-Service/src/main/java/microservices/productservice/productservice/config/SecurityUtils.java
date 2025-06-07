package microservices.productservice.productservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static GatewayAuthenticationFilter.UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof GatewayAuthenticationFilter.UserPrincipal) {
            return (GatewayAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        }
        
        return null;
    }

    public static Long getCurrentUserId() {
        GatewayAuthenticationFilter.UserPrincipal user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUserEmail() {
        GatewayAuthenticationFilter.UserPrincipal user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public static String getCurrentUserRole() {
        GatewayAuthenticationFilter.UserPrincipal user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    public static boolean isAdmin() {
        String role = getCurrentUserRole();
        return "ADMIN".equals(role);
    }

    public static boolean isUser() {
        String role = getCurrentUserRole();
        return "USER".equals(role);
    }
}
