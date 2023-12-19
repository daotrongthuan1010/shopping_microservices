package com.example.gatewayservice.security;

import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class OktaOAuth2WebSecurity {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange((exchanges) ->
                        exchanges
                                .pathMatchers("/admin/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.POST, "/users").hasAuthority("USER_POST")
                                .pathMatchers("/users/{username}").access((authentication, context) ->
                                        authentication
                                                .map(Authentication::getName)
                                                .map((username) -> username.equals(context.getVariables().get("username")))
                                                .map(AuthorizationDecision::new)
                                )
                                .anyExchange().authenticated()
                );
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
