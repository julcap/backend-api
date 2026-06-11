package com.backend.api.models;

public enum OrderStatus {
    PENDING,
    PLACED,
    CONFIRMED,
    PAID,
    PROCESSING,
    FULFILLED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    RETURNED,
    ON_HOLD
}
