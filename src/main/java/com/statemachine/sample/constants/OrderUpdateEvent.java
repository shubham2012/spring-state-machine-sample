package com.statemachine.sample.constants;

public enum OrderUpdateEvent {
    CREATE,
    PROCESS_IN_WAREHOUSE,
    DISPATCH_BY_VENDOR,
    READY_TO_PACK,
    PACK,
    SHIP,
    IN_TRANSIT_SCAN,
    HAND_OVER_TO_DELIVERY_CENTER,
    DELIVERED,
    PICKED_UP_BY_DELIVERY_EXECUTIVE,
    CUSTOMER_NOT_AVAILABLE,
    NOT_ABLE_TO_DELIVER,
    CANCEL
}
