package com.statemachine.sample.constants;

public enum OrderUpdateResponseCode {
    SUCCESS(true),
    ORDER_NOT_FOUND(false),
    VALIDATION_FAILED(false),
    TRANSITION_NOT_ALLOWED(false),
    TRANSITION_NOT_CONFIGURED(false),
    ERROR_DURING_TRANSITION(false),
    WARNING_DURING_TRANSITION(false);


    private boolean orderUpdateSuccess;

    private OrderUpdateResponseCode(boolean success) {
        orderUpdateSuccess = success;
    }

    public boolean getOrderUpdateSuccess(){
        return this.orderUpdateSuccess;
    }
}
