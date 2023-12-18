package com.example.paymentservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentNotFoundException extends RuntimeException{

    private String errorCode;

    public PaymentNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
