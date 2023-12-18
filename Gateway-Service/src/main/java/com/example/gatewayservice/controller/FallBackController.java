package com.example.gatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {

    @GetMapping("/orderServiceFallBack")
    public String orderFallBack(){
        return "orderService da dung, vui long thu lai";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentFallBack(){
        return "paymentService da dung, vui long thu lai";
    }

    @GetMapping("/productServiceFallBack")
    public String productFallBack(){
        return "productService da dung, vui long thu lai";
    }
}
