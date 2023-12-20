package com.example.orderservice.external.intercept;



import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import reactor.util.annotation.NonNull;


import java.io.IOException;
import java.util.Objects;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public RestTemplateInterceptor(
            OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager
                = oAuth2AuthorizedClientManager;
    }

    @Override
    public ClientHttpResponse intercept(@NonNull  HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException, IOException {
        request.getHeaders().add("Authorization",
                "Bearer " +
                        Objects.requireNonNull(oAuth2AuthorizedClientManager
                                        .authorize(OAuth2AuthorizeRequest
                                                .withClientRegistrationId("internal-client")
                                                .principal("internal")
                                                .build()))
                                .getAccessToken().getTokenValue());

        return execution.execute(request, body);
    }
}
