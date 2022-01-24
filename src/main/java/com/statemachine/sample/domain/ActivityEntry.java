package com.statemachine.sample.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.statemachine.sample.constants.EventStatus;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.service.OrderService;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(value = "activity")
public class ActivityEntry {

    private String orderId;

    private OrderStatus fromStatus;

    private OrderService toStatus;

    private OrderUpdateEvent event;

    private EventStatus eventStatus;

    private String remarks;

}
