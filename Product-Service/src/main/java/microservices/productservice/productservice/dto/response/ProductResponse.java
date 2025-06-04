package microservices.productservice.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String status;
    private Long attributeSetId;
    private String attributeSetName;
    private Map<String, Object> attributes;
    private List<String> tags;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean isAvailable;
    private Boolean isFeatured;
    private Boolean isOnSale;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
