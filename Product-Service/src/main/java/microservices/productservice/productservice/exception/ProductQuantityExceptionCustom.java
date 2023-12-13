package microservices.productservice.productservice.exception;

public class ProductQuantityExceptionCustom extends RuntimeException{
    public ProductQuantityExceptionCustom(String message) {
        super(message);
    }
}
