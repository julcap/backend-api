package com.backend.api.service;

import com.backend.api.models.Order;

import java.util.List;

public interface OrderService {
    Order getOrderById(Long id);
    Order saveOrder(Order Order);
    Order updateOrder(Order Order);
    void deleteOrder(Long id);
    List<Order> getOrdersByUserId(Long userId);

    List<Order> getAllOrders();
}
