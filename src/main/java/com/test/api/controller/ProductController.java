package com.test.api.controller;

import jakarta.validation.Valid;
import com.test.api.dto.MessageResponse;
import com.test.api.dto.UpdateProductRequest;
import com.test.api.models.Product;
import com.test.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public void addProduct(@RequestBody Product product) {
        productService.saveProduct(product);
    }

    @PutMapping("/{id}")
    public MessageResponse updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return new MessageResponse("Product not found");
        }
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        productService.saveProduct(existingProduct);
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
