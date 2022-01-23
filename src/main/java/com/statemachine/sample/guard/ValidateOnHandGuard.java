package com.statemachine.sample.guard;

import com.statemachine.sample.domain.OrderEntry;
import com.statemachine.sample.constants.OrderSupplierType;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.constants.StateMachineConstants;
import com.statemachine.sample.exception.GuardHackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidateOnHandGuard implements Guard<OrderStatus, OrderUpdateEvent> {

    @Override
    public boolean evaluate(StateContext<OrderStatus, OrderUpdateEvent> context) {
        log.info("Executing ValidateOnHandGuard for order {}");
        OrderEntry orderEntry = (OrderEntry) context.getMessageHeader(StateMachineConstants.ORDER);
        log.info("Validating inside guard: ValidateOnHandGuard {}", orderEntry.getOrderId());
        if (OrderSupplierType.ON_HAND.equals(orderEntry.getOrderSupplierType())) {
            return true;
        } else {
            throw new GuardHackException("Order of wrong type {}", orderEntry.getOrderId());
        }
    }
}
