package microservices.gatewayservice.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${auth.service.url:http://localhost:8084}")
    private String authServiceUrl;

    private final WebClient webClient;

    public AuthenticationFilter() {
        super(Config.class);
        this.webClient = WebClient.builder().build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            
            // Skip authentication for public endpoints
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            
            return validateToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        log.debug("Token validated successfully for path: {}", path);
                        return chain.filter(exchange);
                    } else {
                        log.warn("Token validation failed for path: {}", path);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error during token validation for path: {}", path, error);
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
        };
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               path.equals("/error") ||
               path.startsWith("/public/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }

    private Mono<Boolean> validateToken(String token) {
        String requestBody = String.format("{\"token\":\"%s\"}", token);

        return webClient.post()
            .uri(authServiceUrl + "/api/auth/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(TokenValidationResponse.class)
            .map(TokenValidationResponse::isValid)
            .doOnSuccess(isValid -> log.debug("Token validation result: {}", isValid))
            .doOnError(error -> log.error("Token validation error: {}", error.getMessage()))
            .onErrorReturn(false);
    }

    public static class Config {
        // Configuration properties if needed
    }

    public static class TokenValidationResponse {
        private boolean valid;
        private Long userId;
        private String email;
        private String role;
        private String message;

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
