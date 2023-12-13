package com.example.orderservice.exception;

public class CustomException extends  RuntimeException{

    private final String errorCode;

    private final int statusCode;

    public CustomException(String errorCode, int statusCode) {
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public CustomException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
