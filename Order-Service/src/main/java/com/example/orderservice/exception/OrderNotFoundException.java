package com.example.orderservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class OrderNotFoundException extends RuntimeException{

    private String errorCode;

    public OrderNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
