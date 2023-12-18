package com.example.paymentservice.controller;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.repsonse.PaymentResponse;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/add")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest request){
    log.info("Start payment order...");
     long paymentId = paymentService.addPayment(request);
        return new ResponseEntity<>(paymentId, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PaymentResponse> getPaymentDetail(@PathVariable Long id){
        log.info("Start get detail payment");
        PaymentResponse paymentResponse = paymentService.findById(id);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

}
