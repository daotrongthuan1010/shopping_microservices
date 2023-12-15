package com.example.orderservice.external;

import com.example.orderservice.external.request.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/api/payment")
public interface PaymentService {

    @PostMapping("/add")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest request);
}
