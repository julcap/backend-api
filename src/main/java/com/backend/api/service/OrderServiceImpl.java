package com.backend.api.service;

import com.backend.api.models.Order;
import com.backend.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository OrderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository OrderRepository) {
        this.OrderRepository = OrderRepository;
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> optionalOrder = OrderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    @Override
    public Order saveOrder(Order Order) {
       return OrderRepository.save(Order);
    }

    @Override
    public Order updateOrder(Order Order) {
        return OrderRepository.save(Order);
    }

    @Override
    public void deleteOrder(Long id) {
        OrderRepository.deleteById(id);
    }

    // Additional method to get all Orders (optional)
    public List<Order> getAllOrders() {
        return OrderRepository.findAll();
    }
}
