package com.backend.api.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long userId;
    private Long quantity;
    private Double unitPrice;
    private Double totalPrice;

    @Builder
    public Order(Long productId, Long userId, Long quantity, Double unitPrice, Double totalPrice) {
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}