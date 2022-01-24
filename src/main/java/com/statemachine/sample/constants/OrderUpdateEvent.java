package com.statemachine.sample.constants;

public enum OrderUpdateEvent {
    CREATE,
    PROCESS_IN_WAREHOUSE,
    DISPATCH_BY_VENDOR,
    PACK,
    SHIP,
    HAND_OVER_TO_DELIVERY_CENTER,
    DELIVERED,
    CUSTOMER_NOT_AVAILABLE,
    NOT_ABLE_TO_DELIVER
}
