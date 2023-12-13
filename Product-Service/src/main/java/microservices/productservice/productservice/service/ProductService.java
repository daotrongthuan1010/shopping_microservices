package microservices.productservice.productservice.service;

import microservices.productservice.productservice.entity.Product;
import microservices.productservice.productservice.model.ProductRequest;
import microservices.productservice.productservice.model.ProductResponse;

public interface ProductService {

    void save(Product product);


    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long id);

    void reduceQuantity(long productId, long quantity);
}
