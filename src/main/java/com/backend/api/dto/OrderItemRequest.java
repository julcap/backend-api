package com.backend.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {

    @NotNull(message = "Product id is required")
    @Positive(message = "Product id must be greater than 0")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Long quantity;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be greater than 0")
    private Double unitPrice;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be greater than 0")
    private Double totalPrice;
}
