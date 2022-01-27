package com.statemachine.sample.handler;

import com.statemachine.sample.domain.OrderUpdate;
import com.statemachine.sample.domain.OrderUpdateResponse;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderType;
import com.statemachine.sample.constants.OrderUpdateEvent;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.transition.TransitionKind;
import org.springframework.transaction.annotation.Transactional;

public interface StateMachineHandler {

    @Transactional(rollbackFor = Exception.class)
    OrderUpdateResponse handleEvent(OrderUpdate orderUpdate) throws Exception;

    Transition<OrderStatus, OrderUpdateEvent> transitionsConfigured(OrderStatus inputSource,
                                                                    OrderUpdateEvent inputEvent, TransitionKind inputTransitionType) throws Exception;
}
