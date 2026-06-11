package com.backend.api.controller;

import com.backend.api.dto.CreateProductRequest;
import jakarta.validation.Valid;
import com.backend.api.dto.MessageResponse;
import com.backend.api.dto.UpdateProductRequest;
import com.backend.api.models.Product;
import com.backend.api.service.ProductService;
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
    public Product addProduct(@Valid CreateProductRequest request) {
        Product product = new Product(request.getName(), request.getDescription(), request.getPrice(), request.getAvailable());
        return productService.saveProduct(product);
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
        existingProduct.setAvailable(request.getAvailable());
        productService.saveProduct(existingProduct);
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
