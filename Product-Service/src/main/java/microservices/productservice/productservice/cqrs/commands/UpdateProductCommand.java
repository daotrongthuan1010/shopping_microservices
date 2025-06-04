package microservices.productservice.productservice.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductCommand {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String name;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private String description;
    
    private Long categoryId;
    
    private Long brandId;
    
    private Map<String, Object> attributeValues;
    
    private String status;
    
    @NotNull(message = "Version is required for optimistic locking")
    private Long version;
}
