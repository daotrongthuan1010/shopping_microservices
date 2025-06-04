package microservices.productservice.productservice.service;

import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.readmodel.ProductReadModel;
import microservices.productservice.productservice.readmodel.ProductSummaryReadModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnMissingBean(name = "productQueryService")
@Slf4j
public class NoOpProductQueryService extends ProductQueryService {

    public NoOpProductQueryService() {
        super(null, null, null); // No repositories needed for no-op
        log.warn("MongoDB not available - using No-Op Product Query Service");
    }

    @Override
    public Optional<ProductReadModel> findById(Long productId) {
        log.debug("No-op: Would find product by ID: {}", productId);
        return Optional.empty();
    }

    @Override
    public Optional<ProductSummaryReadModel> findSummaryById(Long productId) {
        log.debug("No-op: Would find product summary by ID: {}", productId);
        return Optional.empty();
    }

    @Override
    public Page<ProductReadModel> findActiveProducts(Pageable pageable) {
        log.debug("No-op: Would find active products");
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductSummaryReadModel> findActiveProductSummaries(Pageable pageable) {
        log.debug("No-op: Would find active product summaries");
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> findByCategory(Long categoryId, Pageable pageable) {
        log.debug("No-op: Would find products by category: {}", categoryId);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductSummaryReadModel> findSummariesByCategory(Long categoryId, Pageable pageable) {
        log.debug("No-op: Would find summaries by category: {}", categoryId);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> findByBrand(Long brandId, Pageable pageable) {
        log.debug("No-op: Would find products by brand: {}", brandId);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductSummaryReadModel> findSummariesByBrand(Long brandId, Pageable pageable) {
        log.debug("No-op: Would find summaries by brand: {}", brandId);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("No-op: Would find products by price range: {} - {}", minPrice, maxPrice);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> searchProducts(String searchText, Pageable pageable) {
        log.debug("No-op: Would search products with text: '{}'", searchText);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> findAvailableProducts(Pageable pageable) {
        log.debug("No-op: Would find available products");
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public List<ProductSummaryReadModel> findFeaturedProducts() {
        log.debug("No-op: Would find featured products");
        return Collections.emptyList();
    }

    @Override
    public Page<ProductReadModel> findProductsOnSale(Pageable pageable) {
        log.debug("No-op: Would find products on sale");
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> findByFilters(List<Long> categoryIds, List<Long> brandIds, 
                                               BigDecimal minPrice, BigDecimal maxPrice, 
                                               Pageable pageable) {
        log.debug("No-op: Would find products by filters");
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Page<ProductReadModel> findByAttributeValue(String attributeKey, Object attributeValue, Pageable pageable) {
        log.debug("No-op: Would find products by attribute: {} = {}", attributeKey, attributeValue);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public Long countByCategory(Long categoryId) {
        log.debug("No-op: Would count products by category: {}", categoryId);
        return 0L;
    }

    @Override
    public Long countByBrand(Long brandId) {
        log.debug("No-op: Would count products by brand: {}", brandId);
        return 0L;
    }

    @Override
    public List<ProductReadModel> findLowStockProducts(Integer threshold) {
        log.debug("No-op: Would find low stock products with threshold: {}", threshold);
        return Collections.emptyList();
    }
}
