package com.backend.api.controller;

import com.backend.api.dto.CreateOrderRequest;
import com.backend.api.dto.OrderItemRequest;
import com.backend.api.dto.UpdateOrderRequest;
import com.backend.api.models.Order;
import com.backend.api.models.OrderItem;
import com.backend.api.models.Product;
import com.backend.api.models.User;
import com.backend.api.service.OrderService;
import com.backend.api.service.ProductService;
import com.backend.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public ResponseEntity<Order> addOrder(@Valid @RequestBody CreateOrderRequest request) {
        User user = userService.getUserById(request.getUserId());
        Set<OrderItem> orderItems = buildOrderItems(request.getItems());

        if (user == null || orderItems == null) {
            return ResponseEntity.notFound().build();
        }

        Order order = new Order(
                user,
                orderItems,
                request.getStatus(),
                request.getPurchaseDate(),
                request.getShippedDate());

        Order savedOrder = orderService.saveOrder(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest request) {
        Order existingOrder = orderService.getOrderById(id);
        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.getUserById(request.getUserId());
        Set<OrderItem> orderItems = buildOrderItems(request.getItems());

        if (user == null || orderItems == null) {
            return ResponseEntity.notFound().build();
        }

        existingOrder.setUser(user);
        existingOrder.setOrderItems(orderItems);
        existingOrder.setStatus(request.getStatus());
        existingOrder.setPurchaseDate(request.getPurchaseDate());
        existingOrder.setShippedDate(request.getShippedDate());

        Order newOrder = orderService.saveOrder(existingOrder);

        return ResponseEntity.ok(newOrder);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    private Set<OrderItem> buildOrderItems(List<OrderItemRequest> itemRequests) {
        Set<OrderItem> orderItems = new LinkedHashSet<>();

        for (OrderItemRequest itemRequest : itemRequests) {
            Product product = productService.getProductById(itemRequest.getProductId());
            if (product == null) {
                return null;
            }

            orderItems.add(new OrderItem(
                    product,
                    itemRequest.getQuantity(),
                    itemRequest.getUnitPrice(),
                    itemRequest.getTotalPrice()
            ));
        }

        return orderItems;
    }
}
