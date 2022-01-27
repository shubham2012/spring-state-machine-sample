package com.statemachine.sample.constants;

public enum OrderStatus {
    CREATED,
    PROCESSING,
    PACKED,
    SHIPPED,
    RECEIVED_IN_DELIVERY_CENTER,
    OUT_FOR_DELIVERY,
    FAILED_DELIVERY,
    DELIVERED,
    FAILED_DELIVERED,
    CANCELLED
}
