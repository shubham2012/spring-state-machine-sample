package com.statemachine.sample.controller;

import com.statemachine.sample.constants.MessageConstant;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateResponseCode;
import com.statemachine.sample.domain.OrderUpdate;
import com.statemachine.sample.domain.OrderUpdateResponse;
import com.statemachine.sample.handler.OrderStateMachinePersistenceHandler;
import com.statemachine.sample.constants.OrderUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.transition.TransitionKind;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/state-machine")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StateMachineController {

    private final OrderStateMachinePersistenceHandler persistenceHandler;

    @PostMapping(
            value = "/invoke",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity invoke(OrderUpdate update) {
        OrderUpdateResponse orderUpdateResponse;
        try {
            orderUpdateResponse = persistenceHandler.handleEvent(update);
        } catch (Exception e) {
            log.error(MessageConstant.ERROR_DURING_EXECUTION.get(e));
            orderUpdateResponse = new OrderUpdateResponse();
            orderUpdateResponse.setOrderUpdateResponseCode(OrderUpdateResponseCode.ERROR_DURING_TRANSITION);
        }
        return ResponseEntity.ok(orderUpdateResponse);
    }

    @GetMapping(
            value = "/get-valid-transition",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getValidTransition(@RequestParam(value = "inputSource")  OrderStatus inputSource,
                                             @RequestParam(value = "inputEvent")  OrderUpdateEvent inputEvent,
                                             @RequestParam(value = "transitionType")  TransitionKind transitionType) {
        try {
            Transition<OrderStatus, OrderUpdateEvent> orderStatusOrderUpdateEventTransition = persistenceHandler.transitionsConfigured(inputSource,
                    inputEvent, transitionType);
            return ResponseEntity.ok(orderStatusOrderUpdateEventTransition);
        } catch (Exception e) {
            log.error(MessageConstant.ERROR_DURING_EXECUTION.get(e));
            return ResponseEntity.ok(MessageConstant.EVENT_NOT_ACCEPTED.get(e.fillInStackTrace().getMessage()));
        }
    }
}
