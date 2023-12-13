package microservices.productservice.productservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ProductExceptionCustom extends RuntimeException{

    private String errorCode;

    public ProductExceptionCustom(String errorCode) {
        this.errorCode = errorCode;
    }

    public ProductExceptionCustom(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
