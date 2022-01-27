package com.statemachine.sample.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.statemachine.sample.constants.EventStatus;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(value = "activity")
public class ActivityEntry {

    private String orderId;

    private OrderStatus fromStatus;

    private OrderStatus toStatus;

    private OrderUpdateEvent event;

    private EventStatus eventStatus;

    private String remarks;

    private LocalDateTime transitionTime;

}
