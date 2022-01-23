package com.statemachine.sample.action;

import com.statemachine.sample.domain.OrderEntry;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.constants.StateMachineConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessInWarehouseAction implements Action<OrderStatus, OrderUpdateEvent> {

    @Override
    public void execute(StateContext<OrderStatus, OrderUpdateEvent> stateContext) {
        log.info("Executing ProcessInWarehouseAction for order {}");
        OrderEntry orderEntry = (OrderEntry) stateContext.getMessageHeader(StateMachineConstants.ORDER);
        log.info(String.join("############## read the message and now processing order: {}", orderEntry.getOrderId()));
        // take action
    }
}
