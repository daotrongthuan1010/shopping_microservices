package com.example.orderservice.config;

import com.example.orderservice.external.error.CustomErrorDecoder;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class Config {

    @Bean
    ErrorDecoder errorDecoder(){
       return new CustomErrorDecoder();
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
