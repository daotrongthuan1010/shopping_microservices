package com.example.orderservice.config;

import com.example.orderservice.external.error.CustomErrorDecoder;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    @Bean
    ErrorDecoder errorDecoder(){
       return new CustomErrorDecoder();
    }

}
