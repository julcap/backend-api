package com.backend.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Boolean available;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    @Builder
    public Product(String name, String description, Double price, Boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }

}
