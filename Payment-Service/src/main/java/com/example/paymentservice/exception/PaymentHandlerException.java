package com.example.paymentservice.exception;

import com.example.paymentservice.model.MessageErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class PaymentHandlerException {

    @ExceptionHandler(PaymentExceptionCustom.class)
    public ResponseEntity<MessageErrorResponse> handlerNotFoundException(PaymentExceptionCustom exception){

        return new ResponseEntity<>(MessageErrorResponse.builder()
                .messageError(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .build(), HttpStatus.NOT_FOUND);
    }

}
