package microservices.productservice.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(
                        authorizeRequest -> authorizeRequest
                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer(
                        OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }
}
