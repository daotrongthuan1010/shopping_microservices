package microservices.productservice.productservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.dto.response.ApiResponse;
import microservices.productservice.productservice.dto.response.ProductResponse;
import microservices.productservice.productservice.dto.response.ProductSummaryResponse;
import microservices.productservice.productservice.mapper.ProductMapper;
import microservices.productservice.productservice.service.ProductQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductQueryController {
    
    private final ProductQueryService queryService;
    private final ProductMapper productMapper;
    
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long productId) {
        log.info("Getting product: {}", productId);
        
        return queryService.findById(productId)
                .map(product -> ResponseEntity.ok(
                        ApiResponse.success(productMapper.toResponse(product))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{productId}/summary")
    public ResponseEntity<ApiResponse<ProductSummaryResponse>> getProductSummary(@PathVariable Long productId) {
        log.info("Getting product summary: {}", productId);
        
        return queryService.findSummaryById(productId)
                .map(summary -> ResponseEntity.ok(
                        ApiResponse.success(productMapper.toSummaryResponse(summary))))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all products - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductSummaryResponse> products = queryService.findActiveProductSummaries(pageable)
                .map(productMapper::toSummaryResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting products by category: {} - page: {}, size: {}", categoryId, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductSummaryResponse> products = queryService.findSummariesByCategory(categoryId, pageable)
                .map(productMapper::toSummaryResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProductsByBrand(
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting products by brand: {} - page: {}, size: {}", brandId, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductSummaryResponse> products = queryService.findSummariesByBrand(brandId, pageable)
                .map(productMapper::toSummaryResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Searching products with query: '{}' - page: {}, size: {}", q, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = queryService.searchProducts(q, pageable)
                .map(productMapper::toResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Getting products by price range: {} - {} - page: {}, size: {}", 
                minPrice, maxPrice, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = queryService.findByPriceRange(minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAvailableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting available products - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = queryService.findAvailableProducts(pageable)
                .map(productMapper::toResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getFeaturedProducts() {
        log.info("Getting featured products");
        
        List<ProductSummaryResponse> products = queryService.findFeaturedProducts()
                .stream()
                .map(productMapper::toSummaryResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/on-sale")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsOnSale(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting products on sale - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = queryService.findProductsOnSale(pageable)
                .map(productMapper::toResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> filterProducts(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Filtering products - categories: {}, brands: {}, price: {}-{}", 
                categoryIds, brandIds, minPrice, maxPrice);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = queryService.findByFilters(
                categoryIds, brandIds, minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        
        log.info("Getting low stock products with threshold: {}", threshold);
        
        List<ProductResponse> products = queryService.findLowStockProducts(threshold)
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/count/category/{categoryId}")
    public ResponseEntity<ApiResponse<Long>> countByCategory(@PathVariable Long categoryId) {
        log.info("Counting products by category: {}", categoryId);
        
        Long count = queryService.countByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @GetMapping("/count/brand/{brandId}")
    public ResponseEntity<ApiResponse<Long>> countByBrand(@PathVariable Long brandId) {
        log.info("Counting products by brand: {}", brandId);
        
        Long count = queryService.countByBrand(brandId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
