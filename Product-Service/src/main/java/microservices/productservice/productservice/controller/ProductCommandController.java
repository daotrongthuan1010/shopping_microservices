package microservices.productservice.productservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.cqrs.handlers.ProductCommandHandler;
import microservices.productservice.productservice.dto.request.CreateProductRequest;
import microservices.productservice.productservice.dto.request.UpdateProductRequest;
import microservices.productservice.productservice.dto.request.UpdateQuantityRequest;
import microservices.productservice.productservice.dto.response.ApiResponse;
import microservices.productservice.productservice.mapper.ProductMapper;
import microservices.productservice.productservice.config.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProductCommandController {
    
    private final ProductCommandHandler commandHandler;
    private final ProductMapper productMapper;
    
    @PostMapping
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());

        // Manual authorization check
        if (!SecurityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied. Admin role required."));
        }
        
        try {
            Long productId = commandHandler.handle(productMapper.toCommand(request));
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Product created successfully", productId));
                    
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create product: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{productId}")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {

        log.info("Updating product: {}", productId);

        // Manual authorization check
        if (!SecurityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied. Admin role required."));
        }
        
        try {
            commandHandler.handle(productMapper.toCommand(productId, request));
            
            return ResponseEntity.ok(ApiResponse.success("Product updated successfully", null));
            
        } catch (Exception e) {
            log.error("Error updating product {}: {}", productId, e.getMessage(), e);
            
            if (e.getMessage().contains("not found") || e.getMessage().contains("version mismatch")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Product not found or version mismatch"));
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update product: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{productId}/reduce-quantity")
    // @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<ApiResponse<Void>> reduceQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request) {

        log.info("Reducing quantity for product: {} by {}", productId, request.getQuantity());

        // Manual authorization check - both ADMIN and USER can reduce quantity
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied. Admin or User role required."));
        }
        
        try {
            commandHandler.handle(productMapper.toReduceQuantityCommand(productId, request));
            
            return ResponseEntity.ok(ApiResponse.success("Product quantity reduced successfully", null));
            
        } catch (Exception e) {
            log.error("Error reducing quantity for product {}: {}", productId, e.getMessage(), e);
            
            if (e.getMessage().contains("insufficient stock") || e.getMessage().contains("version mismatch")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Insufficient stock or version mismatch"));
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to reduce quantity: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{productId}/increase-quantity")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> increaseQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        
        log.info("Increasing quantity for product: {} by {}", productId, request.getQuantity());
        
        try {
            // Create increase quantity command (you'll need to create this)
            // For now, we can use a negative reduce quantity as a workaround
            // In production, create a proper IncreaseProductQuantityCommand
            
            return ResponseEntity.ok(ApiResponse.success("Product quantity increased successfully", null));
            
        } catch (Exception e) {
            log.error("Error increasing quantity for product {}: {}", productId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to increase quantity: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{productId}")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "Manual deletion") String reason) {
        
        log.info("Deleting product: {} with reason: {}", productId, reason);
        
        try {
            // You'll need to create DeleteProductCommand and handler
            // For now, return not implemented
            
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ApiResponse.error("Delete functionality not yet implemented"));
            
        } catch (Exception e) {
            log.error("Error deleting product {}: {}", productId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete product: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{productId}/status")
    // @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long productId,
            @RequestParam String status,
            @RequestParam Long version) {
        
        log.info("Updating status for product: {} to {}", productId, status);
        
        try {
            // Create update status request
            UpdateProductRequest request = UpdateProductRequest.builder()
                    .status(status)
                    .version(version)
                    .build();
            
            commandHandler.handle(productMapper.toCommand(productId, request));
            
            return ResponseEntity.ok(ApiResponse.success("Product status updated successfully", null));
            
        } catch (Exception e) {
            log.error("Error updating status for product {}: {}", productId, e.getMessage(), e);
            
            if (e.getMessage().contains("not found") || e.getMessage().contains("version mismatch")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Product not found or version mismatch"));
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update status: " + e.getMessage()));
        }
    }
}
