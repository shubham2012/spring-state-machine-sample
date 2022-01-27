package com.statemachine.sample.action;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PackAction implements Action<OrderStatus, OrderUpdateEvent> {

    @Override
    public void execute(StateContext<OrderStatus, OrderUpdateEvent> stateContext) {
        log.info("Executing PackAction for order {}");
        //do pack action or any tagging etc. we need to do
    }
}
