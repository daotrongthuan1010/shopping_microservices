package microservices.productservice.productservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQuantityExceptionCustom extends RuntimeException{

    private String errorCode;

    public ProductQuantityExceptionCustom(String errorCode) {
        this.errorCode = errorCode;
    }

    public ProductQuantityExceptionCustom(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProductQuantityExceptionCustom(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ProductQuantityExceptionCustom(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ProductQuantityExceptionCustom(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
