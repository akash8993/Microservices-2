package com.akash.Product.Service.service;

import com.akash.Product.Service.model.ProductRequest;
import com.akash.Product.Service.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse findProductById(long id);

    void reduceQuantity(long id, long quantity);
}
