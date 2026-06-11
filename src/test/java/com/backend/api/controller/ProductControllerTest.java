package com.backend.api.controller;

import com.backend.api.models.Product;
import com.backend.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .param("name", "Keyboard")
                        .param("description", "Mechanical keyboard")
                        .param("price", "89.99")
                        .param("available", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.description").value("Mechanical keyboard"))
                .andExpect(jsonPath("$.price").value(89.99))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldGetProductById() throws Exception {
        Product product = productRepository.save(
                new Product("Mouse", "Wireless mouse", 49.99, Boolean.TRUE)
        );

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.description").value("Wireless mouse"))
                .andExpect(jsonPath("$.price").value(49.99))
                .andExpect(jsonPath("$.available").value(Boolean.TRUE));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        productRepository.save(new Product("Monitor", "27 inch monitor", 249.99, true));
        productRepository.save(new Product("Desk", "Standing desk", 399.99, false));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Monitor", "Desk")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("27 inch monitor", "Standing desk")))
                .andExpect(jsonPath("$[*].price", containsInAnyOrder(249.99, 399.99)))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(true, false)));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Product product = productRepository.save(
                new Product("Old name", "Old description", 10.0, true)
        );

        String json = """
                {
                  "name": "Updated name",
                  "description": "Updated description",
                  "price": 25.5,
                  "available": false
                }
                """;

        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(25.5))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void shouldReturnProductNotFoundWhenUpdatingMissingProduct() throws Exception {
        String json = """
                {
                  "name": "Updated name",
                  "description": "Updated description",
                  "price": 25.5,
                  "available": true
                }
                """;

        mockMvc.perform(put("/api/products/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Product product = productRepository.save(
                new Product("Headphones", "Noise cancelling headphones", 129.99, true)
        );

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnCreateValidationErrorsForMissingFields() throws Exception {
        mockMvc.perform(post("/api/products"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.description").value("Description is required"))
                .andExpect(jsonPath("$.price").value("Price is required"));
    }

    @Test
    void shouldReturnCreateValidationErrorsForBlankFields() throws Exception {
        mockMvc.perform(post("/api/products")
                        .param("name", "")
                        .param("description", "")
                        .param("price", "20.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.description").value("Description is required"));
    }

    @Test
    void shouldReturnCreateValidationErrorForNonPositivePrice() throws Exception {
        mockMvc.perform(post("/api/products")
                        .param("name", "Keyboard")
                        .param("description", "Mechanical keyboard")
                        .param("price", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be greater than 0"));
    }

    @Test
    void shouldReturnUpdateValidationErrorsForMissingFields() throws Exception {
        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.description").value("Description is required"))
                .andExpect(jsonPath("$.price").value("Price is required"));
    }

    @Test
    void shouldReturnUpdateValidationErrorsForBlankFields() throws Exception {
        String json = """
                {
                  "name": "",
                  "description": "",
                  "price": 20.0
                }
                """;

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.description").value("Description is required"));
    }

    @Test
    void shouldReturnUpdateValidationErrorForNonPositivePrice() throws Exception {
        String json = """
                {
                  "name": "Keyboard",
                  "description": "Mechanical keyboard",
                  "price": 0
                }
                """;

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be greater than 0"));
    }

    @Test
    void shouldReturnUpdateValidationErrorForInvalidPriceValue() throws Exception {
        String json = """
                {
                  "name": "Keyboard",
                  "description": "Mechanical keyboard",
                  "price": "ABC"
                }
                """;

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Invalid value"));
    }
}
