package com.statemachine.sample.constants;

public enum OrderStatus {
    CREATED,
    PROCESSING_IN_WAREHOUSE,
    PACKED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    FAILED_DELIVERY,
    DELIVERED
}
