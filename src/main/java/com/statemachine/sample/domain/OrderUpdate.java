package com.statemachine.sample.domain;

import com.statemachine.sample.constants.OrderUpdateEvent;
import lombok.Data;

@Data
public class OrderUpdate {

    private String orderId;

    private OrderUpdateEvent event;

    private String updatedBy;

    private String remarks;

}
