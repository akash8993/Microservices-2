package com.akash.Product.Service.controller;

import com.akash.Product.Service.model.ProductRequest;
import com.akash.Product.Service.model.ProductResponse;
import com.akash.Product.Service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest)
    {
        long productId= productService.addProduct(productRequest);

        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id)
    {
       ProductResponse productResponse= productService.findProductById(id);
       return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

}
