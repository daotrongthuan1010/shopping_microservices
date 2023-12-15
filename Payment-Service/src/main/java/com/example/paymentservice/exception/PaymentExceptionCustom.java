package com.example.paymentservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentExceptionCustom extends RuntimeException{

    private String errorCode;

    public PaymentExceptionCustom(String errorCode) {
        this.errorCode = errorCode;
    }

    public PaymentExceptionCustom(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
