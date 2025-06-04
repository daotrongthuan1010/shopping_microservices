package microservices.productservice.productservice.repository.read;

import microservices.productservice.productservice.readmodel.ProductReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReadRepository extends MongoRepository<ProductReadModel, String> {
    
    // Find by original product ID
    Optional<ProductReadModel> findByProductId(Long productId);
    
    // Find active products
    @Query("{'status': 'ACTIVE'}")
    Page<ProductReadModel> findActiveProducts(Pageable pageable);
    
    // Find by category
    @Query("{'categoryId': ?0, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findByCategoryIdAndActive(Long categoryId, Pageable pageable);
    
    // Find by brand
    @Query("{'brandId': ?0, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findByBrandIdAndActive(Long brandId, Pageable pageable);
    
    // Price range search
    @Query("{'price': {$gte: ?0, $lte: ?1}, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findByPriceRangeAndActive(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Text search
    @Query("{'$text': {'$search': ?0}, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findByTextSearchAndActive(String searchText, Pageable pageable);
    
    // Find available products (in stock)
    @Query("{'isAvailable': true, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findAvailableProducts(Pageable pageable);
    
    // Find featured products
    @Query("{'isFeatured': true, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findFeaturedProducts(Pageable pageable);
    
    // Find products on sale
    @Query("{'isOnSale': true, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findProductsOnSale(Pageable pageable);
    
    // Complex filter query
    @Query("{'categoryId': {$in: ?0}, 'brandId': {$in: ?1}, 'price': {$gte: ?2, $lte: ?3}, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findByFilters(List<Long> categoryIds, List<Long> brandIds, 
                                        BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Find by attribute values
    @Query("{'attributes.?0': ?1, 'status': 'ACTIVE'}")
    Page<ProductReadModel> findByAttributeValue(String attributeKey, Object attributeValue, Pageable pageable);
    
    // Count by category
    @Query(value = "{'categoryId': ?0, 'status': 'ACTIVE'}", count = true)
    Long countByCategoryIdAndActive(Long categoryId);
    
    // Count by brand
    @Query(value = "{'brandId': ?0, 'status': 'ACTIVE'}", count = true)
    Long countByBrandIdAndActive(Long brandId);
    
    // Find low stock products
    @Query("{'quantity': {$lte: ?0}, 'status': 'ACTIVE'}")
    List<ProductReadModel> findLowStockProducts(Integer threshold);
    
    // Find recently added products
    @Query("{'status': 'ACTIVE'}")
    Page<ProductReadModel> findRecentProducts(Pageable pageable);
}
