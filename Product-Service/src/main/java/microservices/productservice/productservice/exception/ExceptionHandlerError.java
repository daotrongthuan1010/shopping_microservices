package microservices.productservice.productservice.exception;

import microservices.productservice.productservice.model.MessageErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerError extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductExceptionCustom.class)
    public ResponseEntity<MessageErrorResponse> handlerNotFoundException(){

        return new ResponseEntity<>(MessageErrorResponse.builder()
                .messageError("Khong tim thay san pham")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductQuantityExceptionCustom.class)
    public ResponseEntity<MessageErrorResponse> handlerQuantityProductException(){
        return new ResponseEntity<>(MessageErrorResponse.builder()
                .messageError("San pham da het hang")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.NOT_FOUND);
    }
}
