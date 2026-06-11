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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
    void shouldCreateUser() throws Exception {
        String json = """
                {
                  "name": "John",
                  "surname": "Doe",
                  "phone": "12345678",
                  "birthdate": "1990-05-20",
                  "country": "Denmark",
                  "address": "Some address"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.phone").value("12345678"))
                .andExpect(jsonPath("$.birthdate").value("1990-05-20"))
                .andExpect(jsonPath("$.country").value("Denmark"))
                .andExpect(jsonPath("$.address").value("Some address"));
    }


    @Test
    void shouldReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Name is required"))
                .andExpect(jsonPath("$.surname")
                        .value("Surname is required"))
                .andExpect(jsonPath("$.birthdate")
                        .value("Date of birth is required"));
    }

    @Test
    void shouldRejectInvalidBirthdateFormat() throws Exception {
        String json = """
            {
              "name": "John",
              "surname": "Doe",
              "birthdate": "ABC"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthdate")
                        .value("Invalid date format. Expected format: yyyy-MM-dd"));
    }

    @Test
    void shouldRejectFutureBirthdate() throws Exception {
        String json = """
            {
              "name": "John",
              "surname": "Doe",
              "birthdate": "%s"
            }
            """.formatted(
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthdate")
                        .value("Date of birth must be a past date"));
    }

    @Test
    void shouldRejectBlankName() throws Exception {
        String json = """
            {
              "name": "",
              "surname": "Doe",
              "birthdate": "1990-05-20"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Name is required"));
    }

    @Test
    void shouldRejectBlankSurname() throws Exception {
        String json = """
            {
              "name": "John",
              "surname": "",
              "birthdate": "1990-05-20"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.surname")
                        .value("Surname is required"));
    }

    @Test
    void shouldGetUserOrders() throws Exception {
        User user = userRepository.save(new User(
                "John",
                "Doe",
                "12345678",
                LocalDate.of(1990, 5, 20),
                "Denmark",
                "Some address"
        ));
        Product product = productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                89.99,
                true
        ));
        OrderItem orderItem = new OrderItem(product, 2L, 89.99, 179.98);
        Order order = orderRepository.save(new Order(
                user,
                Set.of(orderItem),
                OrderStatus.PLACED,
                LocalDate.of(2026, 6, 11),
                null
        ));

        mockMvc.perform(get("/api/users/{id}/orders", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order.getId()))
                .andExpect(jsonPath("$[0].status").value("PLACED"))
                .andExpect(jsonPath("$[0].purchaseDate").value("2026-06-11"))
                .andExpect(jsonPath("$[0].user.id").value(user.getId()))
                .andExpect(jsonPath("$[0].orderItems[0].quantity").value(2))
                .andExpect(jsonPath("$[0].orderItems[0].unitPrice").value(89.99))
                .andExpect(jsonPath("$[0].orderItems[0].totalPrice").value(179.98))
                .andExpect(jsonPath("$[0].orderItems[0].product.id").value(product.getId()))
                .andExpect(jsonPath("$[0].orderItems[0].product.name").value("Keyboard"));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoOrders() throws Exception {
        User user = userRepository.save(new User(
                "John",
                "Doe",
                "12345678",
                LocalDate.of(1990, 5, 20),
                "Denmark",
                "Some address"
        ));

        mockMvc.perform(get("/api/users/{id}/orders", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenGettingOrdersForMissingUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}/orders", 999L))
                .andExpect(status().isNotFound());
    }
}
