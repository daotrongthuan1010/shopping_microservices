package microservices.productservice.productservice.repository.write;

import microservices.productservice.productservice.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ProductWriteRepository extends JpaRepository<Product, Long> {
    
    // Pessimistic locking for concurrent updates
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
    
    // Optimistic locking check
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.version = :version")
    Optional<Product> findByIdAndVersion(@Param("id") Long id, @Param("version") Long version);
    
    // Bulk quantity update for performance
    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity - :quantity, p.version = p.version + 1 " +
           "WHERE p.id = :id AND p.quantity >= :quantity AND p.version = :version")
    int reduceQuantity(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Long version);
    
    @Modifying
    @Query("UPDATE Product p SET p.quantity = p.quantity + :quantity, p.version = p.version + 1 " +
           "WHERE p.id = :id AND p.version = :version")
    int increaseQuantity(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Long version);
    
    // Status updates
    @Modifying
    @Query("UPDATE Product p SET p.status = :status, p.version = p.version + 1 " +
           "WHERE p.id = :id AND p.version = :version")
    int updateStatus(@Param("id") Long id, @Param("status") Product.ProductStatus status, @Param("version") Long version);
    
    // Check if product exists and is active
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.id = :id AND p.status = 'ACTIVE'")
    boolean existsByIdAndActive(@Param("id") Long id);
    
    // Find products with low stock
    @Query("SELECT p FROM Product p WHERE p.quantity <= :threshold AND p.status = 'ACTIVE'")
    java.util.List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}
