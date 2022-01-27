package com.statemachine.sample.guard;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminRoleGuard implements Guard<OrderStatus, OrderUpdateEvent> {

    @Override
    public boolean evaluate(StateContext<OrderStatus, OrderUpdateEvent> context) {
        log.info("Executing AdminRoleGuard for order {}");
        // validate admin role guard
        return true;
    }
}
