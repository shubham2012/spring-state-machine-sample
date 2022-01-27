package com.statemachine.sample.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true, value = {"exception"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderUpdateResponse {

    private OrderUpdate orderUpdate;

    private OrderUpdateResponseCode orderUpdateResponseCode;

    private OrderStatus orderStatusPreEvent;

    private OrderStatus orderStatusPostEvent;

    private Exception exception;

    private String exceptionMessage;

    public OrderUpdateResponse(OrderUpdate orderUpdate) {
        this.orderUpdate = orderUpdate;
    }

    public OrderUpdateResponse() {
    }
}
