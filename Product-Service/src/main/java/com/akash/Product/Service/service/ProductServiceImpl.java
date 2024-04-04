package com.akash.Product.Service.service;

import com.akash.Product.Service.entity.Product;
import com.akash.Product.Service.exception.ProductServiceCustomException;
import com.akash.Product.Service.model.ProductRequest;
import com.akash.Product.Service.model.ProductResponse;
import com.akash.Product.Service.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product");
        Product product=Product.builder().productName(productRequest.getName())
                        .price(productRequest.getPrice())
                                .quantity(productRequest.getQuantity())
                                        .build();

       Product product1= productRepository.save(product);
       log.info("Product Saved");
       return product1.getProductId();
    }

    @Override
    public ProductResponse findProductById(long id) {
        Product product= productRepository.findById(id).orElseThrow(()-> new ProductServiceCustomException("Data not found","PRODUCT NOT FOUND"));
        log.info("Found Product with"+product);

        ProductResponse response= ProductResponse.builder().productName(product.getProductName())
                .productId(product.getProductId())
                .quantity(product.getQuantity())
                .price(product.getPrice()).build();

        return response;
    }

    @Override
    public void reduceQuantity(long id, long quantity) {
        Product product= productRepository.findById(id).orElseThrow(()-> new ProductServiceCustomException("Product with given Id not found","PRODUCT_NOT_FOUND"));
        log.info("Found Product with"+product);
        if(product.getQuantity()<quantity)
        {
                throw new ProductServiceCustomException("Product Does not have sufficient quantity","INSUFFICIENT_QUANTITY");
        }
        else
        {
            product.setQuantity(product.getQuantity()-quantity);
            log.info("Reduced Quantity by {}", product.getQuantity()-quantity);
            productRepository.save(product);
        }

    }
}
