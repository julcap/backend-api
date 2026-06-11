package com.backend.api.dto;

import com.backend.api.models.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "User Id is required")
    @Positive(message = "User Id must be greater than 0")
    private Long userId;

    @NotEmpty(message = "Order items are required")
    private List<@Valid OrderItemRequest> items;

    private OrderStatus status;

    private LocalDate purchaseDate;

    private LocalDate shippedDate;
}
