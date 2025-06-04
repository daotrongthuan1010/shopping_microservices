package microservices.gatewayservice.gatewayservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/orderServiceFallBack")
    public ResponseEntity<String> orderServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Order Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/paymentServiceFallBack")
    public ResponseEntity<String> paymentServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/productServiceFallBack")
    public ResponseEntity<String> productServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Product Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/authServiceFallBack")
    public ResponseEntity<String> authServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth Service is temporarily unavailable. Please try again later.");
    }
}
