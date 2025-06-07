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
                .flatMap(tokenResponse -> {
                    if (tokenResponse.isValid()) {
                        log.debug("Token validated successfully for path: {}", path);

                        // Add user information to headers for downstream services
                        var mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(tokenResponse.getUserId()))
                            .header("X-User-Email", tokenResponse.getEmail())
                            .header("X-User-Role", tokenResponse.getRole())
                            .build();

                        var mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                        return chain.filter(mutatedExchange);
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

    private Mono<TokenValidationResponse> validateToken(String token) {
        String requestBody = String.format("{\"token\":\"%s\"}", token);

        return webClient.post()
            .uri(authServiceUrl + "/api/auth/validate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(TokenValidationResponse.class)
            .doOnSuccess(response -> log.debug("Token validation result: {}", response.isValid()))
            .doOnError(error -> log.error("Token validation error: {}", error.getMessage()))
            .onErrorReturn(new TokenValidationResponse(false, "Validation service error"));
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

        // Default constructor
        public TokenValidationResponse() {}

        // Constructor for error cases
        public TokenValidationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

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
