package microservices.productservice.productservice.cqrs.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCommand {
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;
    
    private String description;
    
    private Long categoryId;
    
    private Long brandId;
    
    private Long attributeSetId;
    
    private Map<String, Object> attributeValues;
    
    @Builder.Default
    private String status = "ACTIVE";
}
