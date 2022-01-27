package com.statemachine.sample.guard;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.constants.StateMachineConstants;
import com.statemachine.sample.domain.OrderEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReceiveInDeliveryCenterGuard implements Guard<OrderStatus, OrderUpdateEvent> {

    @Override
    public boolean evaluate(StateContext<OrderStatus, OrderUpdateEvent> context) {
        log.info("Executing ReceiveInDeliveryCenterGuard for order {}");
        OrderEntry orderEntry = (OrderEntry) context.getMessageHeader(StateMachineConstants.ORDER);
        log.info("Validating inside guard: ReceiveInDeliveryCenterGuard {}", orderEntry.getId());
        //validate if received in the correct location having type as delivery center and also
        // it reached to the correct location or not
        return true;
    }
}
