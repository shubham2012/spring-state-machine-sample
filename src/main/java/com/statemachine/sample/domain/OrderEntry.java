package com.statemachine.sample.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.statemachine.sample.constants.OrderSupplierType;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(value = "order")
public class OrderEntry {

    private OrderStatus status;

    private OrderType type;

    private String remarks;

    private String orderId;

    private OrderSupplierType orderSupplierType;

    private String lastUpdatedBy;
}
