package com.backend.api.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    private String status;
    private LocalDate purchaseDate;
    private LocalDate shippedDate;

    @Builder
    public Order(User user, Set<OrderItem> orderItems, String status, LocalDate purchaseDate, LocalDate shippedDate) {
        this.user = user;
        setOrderItems(orderItems);
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.shippedDate = shippedDate;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems.clear();
        if (orderItems == null) {
            return;
        }

        orderItems.stream()
                .filter(Objects::nonNull)
                .forEach(this::addOrderItem);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
