package microservices.productservice.productservice.exception;

import lombok.Data;

public class ProductExceptionCustom extends RuntimeException{

    private String errorCode;

    public ProductExceptionCustom(String errorCode) {
        this.errorCode = errorCode;
    }

    public ProductExceptionCustom(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProductExceptionCustom(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ProductExceptionCustom(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ProductExceptionCustom(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
