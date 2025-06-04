package microservices.authservice.authservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.authservice.authservice.entity.User;
import microservices.authservice.authservice.service.RefreshTokenService;
import microservices.authservice.authservice.service.UserService;
import microservices.authservice.authservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        try {
            // Create or update user
            User user = userService.createOrUpdateUserFromOAuth2(oAuth2User);
            
            // Generate JWT tokens
            String accessToken = jwtUtil.generateToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user.getId());
            
            // Redirect to frontend with tokens
            String targetUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("token", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();
            
            log.info("OAuth2 login successful for user: {}", user.getEmail());
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            
        } catch (Exception e) {
            log.error("Error during OAuth2 login success handling", e);
            
            String errorUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("error", "authentication_failed")
                    .build().toUriString();
            
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}
