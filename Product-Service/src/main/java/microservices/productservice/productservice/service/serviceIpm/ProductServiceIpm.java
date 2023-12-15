package microservices.productservice.productservice.service.serviceIpm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.entity.Product;
import microservices.productservice.productservice.exception.ProductExceptionCustom;
import microservices.productservice.productservice.exception.ProductQuantityExceptionCustom;
import microservices.productservice.productservice.model.ProductRequest;
import microservices.productservice.productservice.model.ProductResponse;
import microservices.productservice.productservice.repository.ProductRepository;
import microservices.productservice.productservice.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceIpm implements ProductService {


    private final ProductRepository productRepository;

    @Override
    public void save(Product product) {

    }

    @Override
    @Transactional
    public long addProduct(ProductRequest productRequest) {

        log.info("Adding product...");

        productRepository.save(Product.builder()
                        .price(productRequest.getPrice())
                        .name(productRequest.getName())
                        .quantity(productRequest.getQuantity())
                .build());

        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        log.info("Getting product...");

       Product product =  productRepository.findById(id).orElseThrow(() -> new ProductExceptionCustom(
               "Khong tim thay san pham",
               "PRODUCT_NOT_FOUND"));

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    @Override
    @Transactional
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for Id: {}", quantity, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductExceptionCustom("San pham khong tim thay", "NOT_FOUND"));
        if(product.getQuantity() < quantity){
           throw new ProductQuantityExceptionCustom("San pham het hang","IN");
        }
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
    }
}
