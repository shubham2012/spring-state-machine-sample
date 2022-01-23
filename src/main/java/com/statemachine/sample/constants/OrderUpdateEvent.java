package com.statemachine.sample.constants;

public enum OrderUpdateEvent {
    CREATE,
    RECEIVED_IN_WAREHOUSE,
    PACK,
    SHIP,
    HAND_OVER_TO_DE,
    DELIVERED,
    CUSTOMER_NOT_AVAILABLE,
    NOT_ABLE_TO_DELIVER
}
