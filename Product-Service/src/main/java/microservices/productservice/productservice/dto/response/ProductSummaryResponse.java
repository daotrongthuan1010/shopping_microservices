package microservices.productservice.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponse {
    
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String status;
    private String imageUrl;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean isAvailable;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
