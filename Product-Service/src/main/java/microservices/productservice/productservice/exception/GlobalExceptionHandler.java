package microservices.productservice.productservice.exception;

import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", "VALIDATION_ERROR"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        
        log.warn("Constraint violation: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Constraint violation: " + ex.getMessage(), "CONSTRAINT_VIOLATION"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        
        log.warn("Type mismatch: {}", ex.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message, "TYPE_MISMATCH"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), "ILLEGAL_ARGUMENT"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        
        // Check for specific business logic errors
        if (ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage(), "NOT_FOUND"));
        }
        
        if (ex.getMessage().contains("version mismatch") || ex.getMessage().contains("optimistic locking")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Resource has been modified by another user", "OPTIMISTIC_LOCK_ERROR"));
        }
        
        if (ex.getMessage().contains("insufficient")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage(), "INSUFFICIENT_RESOURCE"));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }
}
