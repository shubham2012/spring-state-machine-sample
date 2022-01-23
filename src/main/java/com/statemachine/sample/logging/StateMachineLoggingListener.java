package com.statemachine.sample.logging;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.statemachine.sample.constants.MessageConstant.*;

@Slf4j
@Service
public class StateMachineLoggingListener implements StateMachineListener<OrderStatus, OrderUpdateEvent> {
    
    @Override
    public void transitionStarted(Transition<OrderStatus, OrderUpdateEvent> transition) {
        State<OrderStatus, OrderUpdateEvent> source = transition.getSource();
        State<OrderStatus, OrderUpdateEvent> target = transition.getTarget();
        if(Objects.nonNull(source) && Objects.nonNull(target)){
            log.debug(TRANSITION_STARTED.get(source.getId(), transition.getTrigger().getEvent(), target.getId()));
        }
    }

    @Override
    public void transitionEnded(Transition<OrderStatus, OrderUpdateEvent> transition) {
        State<OrderStatus, OrderUpdateEvent> source = transition.getSource();
        State<OrderStatus, OrderUpdateEvent> target = transition.getTarget();
        if(Objects.nonNull(source) && Objects.nonNull(target)){
            log.debug(TRANSITION_ENDED.get(source.getId(), transition.getTrigger().getEvent(), target.getId()));
        }
    }

    @Override
    public void transition(Transition<OrderStatus, OrderUpdateEvent> transition) {
        State<OrderStatus, OrderUpdateEvent> source = transition.getSource();
        State<OrderStatus, OrderUpdateEvent> target = transition.getTarget();
        if(Objects.nonNull(source) && Objects.nonNull(target)){
            log.debug(TRANSITION.get(source.getId(), transition.getTrigger().getEvent(), target.getId()));
        }
    }

    @Override
    public void stateMachineStopped(StateMachine<OrderStatus, OrderUpdateEvent> stateMachine) {
        log.debug(STATE_MACHINE_STOPPED.get(stateMachine.getState().getId()));
    }

    @Override
    public void stateMachineStarted(StateMachine<OrderStatus, OrderUpdateEvent> stateMachine) {
        log.debug(STATE_MACHINE_STARTED.get(stateMachine.getState().getId()));
    }

    @Override
    public void stateMachineError(StateMachine<OrderStatus, OrderUpdateEvent> stateMachine, Exception exception) {
        log.error(STATE_MACHINE_ERROR.get(stateMachine.getState().getId(), exception));
    }

    @Override
    public void stateExited(State<OrderStatus, OrderUpdateEvent> state) {
        log.debug(STATE_EXISTED.get(state.getId()));
    }

    @Override
    public void stateEntered(State<OrderStatus, OrderUpdateEvent> state) {
        log.debug(STATE_ENTERED.get(state.getId()));
    }

    @Override
    public void stateChanged(State<OrderStatus, OrderUpdateEvent> from, State<OrderStatus, OrderUpdateEvent> to) {
        log.debug(STATE_CHANGED.get(from, to));
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
    }

    @Override
    public void stateContext(StateContext<OrderStatus, OrderUpdateEvent> stateContext) {
    }

    @Override
    public void eventNotAccepted(Message<OrderUpdateEvent> event) {
        log.debug(EVENT_NOT_ACCEPTED.get(event.getPayload(), event.getHeaders().get("orderUpdate")));
    }

}
