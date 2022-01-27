package com.statemachine.sample.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.statemachine.sample.constants.OrderUpdateEvent;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(value = "orderUpdate")
public class OrderUpdate {

    @NotNull(message = "orderId can not be null")
    private String orderId;

    @NotNull(message = "OrderUpdateEvent can not be null")
    private OrderUpdateEvent event;

    @NotNull(message = "updatedBy can not be null")
    private String updatedBy;

    private String remarks;

    private String location;

}
