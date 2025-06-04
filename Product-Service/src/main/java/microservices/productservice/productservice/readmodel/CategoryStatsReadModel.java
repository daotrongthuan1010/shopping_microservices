package microservices.productservice.productservice.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "category_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsReadModel {
    
    @Id
    private String id;

    private Long categoryId;
    
    private String categoryName;
    
    private Integer totalProducts;
    
    private Integer activeProducts;
    
    private Integer inStockProducts;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private BigDecimal avgPrice;
    
    private Integer totalQuantity;
    
    private Double avgRating;

    private LocalDateTime lastUpdated;
}
