package com.backend.api.controller;

import com.backend.api.models.Order;
import com.backend.api.models.OrderItem;
import com.backend.api.models.OrderStatus;
import com.backend.api.models.Product;
import com.backend.api.models.User;
import com.backend.api.repository.OrderRepository;
import com.backend.api.repository.ProductRepository;
import com.backend.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

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
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        deleteTestData();
    }

    @AfterEach
    void tearDown() {
        deleteTestData();
    }

    private void deleteTestData() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() throws Exception {
        User user = saveUser();
        Product keyboard = saveProduct("Keyboard", 89.99);
        Product mouse = saveProduct("Mouse", 49.99);

        String json = """
                {
                  "userId": %d,
                  "items": [
                    {
                      "productId": %d,
                      "quantity": 2,
                      "unitPrice": 89.99,
                      "totalPrice": 179.98
                    },
                    {
                      "productId": %d,
                      "quantity": 1,
                      "unitPrice": 49.99,
                      "totalPrice": 49.99
                    }
                  ],
                  "status": "PLACED",
                  "purchaseDate": "2026-06-11",
                  "shippedDate": null
                }
                """.formatted(user.getId(), keyboard.getId(), mouse.getId());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.purchaseDate").value("2026-06-11"))
                .andExpect(jsonPath("$.orderItems", hasSize(2)))
                .andExpect(jsonPath("$.orderItems[*].quantity", containsInAnyOrder(2, 1)))
                .andExpect(jsonPath("$.orderItems[*].unitPrice", containsInAnyOrder(89.99, 49.99)))
                .andExpect(jsonPath("$.orderItems[*].totalPrice", containsInAnyOrder(179.98, 49.99)))
                .andExpect(jsonPath("$.orderItems[*].product.name", containsInAnyOrder("Keyboard", "Mouse")));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        User user = saveUser();
        Product product = saveProduct("Keyboard", 89.99);
        Order order = saveOrder(user, product, OrderStatus.PAID);

        mockMvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.purchaseDate").value("2026-06-11"))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2))
                .andExpect(jsonPath("$.orderItems[0].unitPrice").value(89.99))
                .andExpect(jsonPath("$.orderItems[0].totalPrice").value(179.98))
                .andExpect(jsonPath("$.orderItems[0].product.id").value(product.getId()))
                .andExpect(jsonPath("$.orderItems[0].product.name").value("Keyboard"));
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        User user = saveUser();
        Product keyboard = saveProduct("Keyboard", 89.99);
        Product mouse = saveProduct("Mouse", 49.99);
        saveOrder(user, keyboard, OrderStatus.PLACED);
        saveOrder(user, mouse, OrderStatus.FULFILLED);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder("PLACED", "FULFILLED")))
                .andExpect(jsonPath("$[*].user.id", containsInAnyOrder(user.getId().intValue(), user.getId().intValue())));
    }

    @Test
    void shouldUpdateOrder() throws Exception {
        User user = saveUser();
        Product oldProduct = saveProduct("Keyboard", 89.99);
        Product newProduct = saveProduct("Mouse", 49.99);
        Order order = saveOrder(user, oldProduct, OrderStatus.PLACED);

        String json = """
                {
                  "userId": %d,
                  "items": [
                    {
                      "productId": %d,
                      "quantity": 3,
                      "unitPrice": 49.99,
                      "totalPrice": 149.97
                    }
                  ],
                  "status": "SHIPPED",
                  "purchaseDate": "2026-06-11",
                  "shippedDate": "2026-06-12"
                }
                """.formatted(user.getId(), newProduct.getId());

        mockMvc.perform(put("/api/orders/{id}", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value("SHIPPED"))
                .andExpect(jsonPath("$.shippedDate").value("2026-06-12"))
                .andExpect(jsonPath("$.orderItems", hasSize(1)))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(3))
                .andExpect(jsonPath("$.orderItems[0].unitPrice").value(49.99))
                .andExpect(jsonPath("$.orderItems[0].totalPrice").value(149.97))
                .andExpect(jsonPath("$.orderItems[0].product.id").value(newProduct.getId()))
                .andExpect(jsonPath("$.orderItems[0].product.name").value("Mouse"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingOrder() throws Exception {
        User user = saveUser();
        Product product = saveProduct("Keyboard", 89.99);

        String json = validOrderJson(user.getId(), product.getId(), "PLACED");

        mockMvc.perform(put("/api/orders/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingOrderForMissingUser() throws Exception {
        Product product = saveProduct("Keyboard", 89.99);

        String json = validOrderJson(999L, product.getId(), "PLACED");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingOrderWithMissingProduct() throws Exception {
        User user = saveUser();

        String json = validOrderJson(user.getId(), 999L, "PLACED");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        User user = saveUser();
        Product product = saveProduct("Keyboard", 89.99);
        Order order = saveOrder(user, product, OrderStatus.PLACED);

        mockMvc.perform(delete("/api/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnValidationErrorsForMissingFields() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("User Id is required"))
                .andExpect(jsonPath("$.items").value("Order items are required"));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidOrderItemFields() throws Exception {
        User user = saveUser();

        String json = """
                {
                  "userId": %d,
                  "items": [
                    {
                      "productId": 0,
                      "quantity": 0,
                      "unitPrice": 0,
                      "totalPrice": 0
                    }
                  ],
                  "status": "PLACED"
                }
                """.formatted(user.getId());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['items[0].productId']").value("Product id must be greater than 0"))
                .andExpect(jsonPath("$['items[0].quantity']").value("Quantity must be greater than 0"))
                .andExpect(jsonPath("$['items[0].unitPrice']").value("Unit price must be greater than 0"))
                .andExpect(jsonPath("$['items[0].totalPrice']").value("Total price must be greater than 0"));
    }

    @Test
    void shouldReturnValidationErrorForInvalidStatusValue() throws Exception {
        User user = saveUser();
        Product product = saveProduct("Keyboard", 89.99);

        String json = validOrderJson(user.getId(), product.getId(), "NOT_A_STATUS");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(
                        "Invalid value. Valid values are: PENDING, PLACED, CONFIRMED, PAID, PROCESSING, FULFILLED, SHIPPED, DELIVERED, CANCELLED, FAILED, REFUNDED, PARTIALLY_REFUNDED, RETURNED, ON_HOLD"
                ));
    }

    private User saveUser() {
        return userRepository.save(new User(
                "John",
                "Doe",
                "12345678",
                LocalDate.of(1990, 5, 20),
                "Denmark",
                "Some address"
        ));
    }

    private Product saveProduct(String name, Double price) {
        return productRepository.save(new Product(
                name,
                name + " description",
                price,
                true
        ));
    }

    private Order saveOrder(User user, Product product, OrderStatus status) {
        OrderItem orderItem = new OrderItem(product, 2L, product.getPrice(), product.getPrice() * 2);
        return orderRepository.save(new Order(
                user,
                Set.of(orderItem),
                status,
                LocalDate.of(2026, 6, 11),
                null
        ));
    }

    private String validOrderJson(Long userId, Long productId, String status) {
        return """
                {
                  "userId": %d,
                  "items": [
                    {
                      "productId": %d,
                      "quantity": 2,
                      "unitPrice": 89.99,
                      "totalPrice": 179.98
                    }
                  ],
                  "status": "%s",
                  "purchaseDate": "2026-06-11",
                  "shippedDate": null
                }
                """.formatted(userId, productId, status);
    }
}
