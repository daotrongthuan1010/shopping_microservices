package microservices.productservice.productservice.repository.read;

import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSummaryReadRepository extends MongoRepository<ProductSummaryReadModel, String> {
    
    Optional<ProductSummaryReadModel> findByProductId(Long productId);
    
    @Query("{'status': 'ACTIVE'}")
    Page<ProductSummaryReadModel> findActiveProducts(Pageable pageable);
    
    @Query("{'categoryId': ?0, 'status': 'ACTIVE'}")
    Page<ProductSummaryReadModel> findByCategoryIdAndActive(Long categoryId, Pageable pageable);
    
    @Query("{'brandId': ?0, 'status': 'ACTIVE'}")
    Page<ProductSummaryReadModel> findByBrandIdAndActive(Long brandId, Pageable pageable);
    
    @Query("{'isFeatured': true, 'status': 'ACTIVE'}")
    List<ProductSummaryReadModel> findFeaturedProducts();
    
    @Query("{'isAvailable': true, 'status': 'ACTIVE'}")
    Page<ProductSummaryReadModel> findAvailableProducts(Pageable pageable);
}
