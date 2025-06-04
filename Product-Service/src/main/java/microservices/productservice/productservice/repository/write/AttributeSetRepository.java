package microservices.productservice.productservice.repository.write;

import microservices.productservice.productservice.domain.entity.AttributeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeSetRepository extends JpaRepository<AttributeSet, Long> {
    
    Optional<AttributeSet> findByName(String name);
    
    @Query("SELECT a FROM AttributeSet a LEFT JOIN FETCH a.attributes WHERE a.id = :id")
    Optional<AttributeSet> findByIdWithAttributes(@Param("id") Long id);
    
    boolean existsByName(String name);
}
