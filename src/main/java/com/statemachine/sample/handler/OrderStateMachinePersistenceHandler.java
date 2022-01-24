package com.statemachine.sample.handler;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderSupplierType;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.constants.StateMachineConstants;
import com.statemachine.sample.domain.OrderEntry;
import com.statemachine.sample.domain.OrderUpdate;
import com.statemachine.sample.domain.OrderUpdateResponse;
import com.statemachine.sample.exception.GuardHackException;
import com.statemachine.sample.service.OrderService;
import com.statemachine.sample.transition.OrderStateMachineTransitionConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.transition.TransitionKind;
import org.springframework.statemachine.trigger.Trigger;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderStateMachinePersistenceHandler extends StateMachineInterceptorAdapter<OrderStatus, OrderUpdateEvent>
        implements StateMachinePersistenceHandler {

    private final OrderStateMachineTransitionConfiguration orderStateMachineTransitionConfiguration;

    private final OrderService orderService;

    @Override
    public OrderUpdateResponse handleEvent(OrderUpdate orderUpdate) throws Exception {
        //validate if order exists or not and accordingly return the response
        OrderEntry orderEntry = orderService.getById(orderUpdate.getOrderId());
        //validate();

        OrderUpdateResponse orderUpdateResponse = invokeStateMachine(orderUpdate, orderUpdate.getEvent(),
                orderEntry);

        return orderUpdateResponse;
    }

    private OrderUpdateResponse invokeStateMachine(OrderUpdate orderUpdate, OrderUpdateEvent event, OrderEntry orderEntry) {
        OrderUpdateResponse response = new OrderUpdateResponse(orderUpdate);
        try {
            StateMachine<OrderStatus, OrderUpdateEvent> stateMachine =
                    loadStateMachineWithState(orderEntry.getStatus());

            Message<OrderUpdateEvent> message = MessageBuilder.withPayload(event)
                    .setHeader(StateMachineConstants.ORDER_UPDATE, orderUpdate)
                    .setHeader(StateMachineConstants.ORDER, orderEntry)
                    .build();
            boolean stateTransition = stateMachine.sendEvent(message);
            if (stateTransition) {
                response.setOrderStatusPostEvent(stateMachine.getState().getId());
            } else {
                log.warn("{} not defined for orderId: {}, and status: {}",
                        event, orderEntry.getOrderId(), orderEntry.getStatus());
                // compile and return proper error response
            }
        } catch (GuardHackException ex) {
            log.warn("Transition not allowed for order {}", orderEntry.getOrderId(), ex);
            //response.setShipmentUpdateResponseCode(ShipmentUpdateResponseCode.TRANSITION_NOT_ALLOWED);
            //response.setException(ex);
        } catch (Exception ex) {
            log.error("Transition error occurred for order {}", orderEntry.getOrderId(), ex);
            //response.setShipmentUpdateResponseCode(ShipmentUpdateResponseCode.ERROR_DURING_TRANSITION);
            response.setException(ex);
        } finally {
            // do log in the activity table
            // doLogActivity();
        }
        return response;
    }

    public StateMachine<OrderStatus, OrderUpdateEvent> loadStateMachineWithState(OrderStatus orderStatus) throws Exception{
        StateMachine<OrderStatus, OrderUpdateEvent> stateMachine = orderStateMachineTransitionConfiguration.getStateMachine();
        stateMachine.stop();
        List<StateMachineAccess<OrderStatus, OrderUpdateEvent>> withAllRegions =
                stateMachine.getStateMachineAccessor().withAllRegions();
        for (StateMachineAccess<OrderStatus, OrderUpdateEvent> a : withAllRegions) {
            a.resetStateMachine(new DefaultStateMachineContext<OrderStatus, OrderUpdateEvent>(orderStatus, null, null, null));
        }
        final StateMachineInterceptor<OrderStatus, OrderUpdateEvent> stateMachineInterceptor = this;
        stateMachine.getStateMachineAccessor().doWithAllRegions(new StateMachineFunction<StateMachineAccess<OrderStatus, OrderUpdateEvent>>() {
            @Override
            public void apply(StateMachineAccess<OrderStatus, OrderUpdateEvent> function) {
                function.addStateMachineInterceptor(stateMachineInterceptor);
            }
        });
        stateMachine.start();
        return stateMachine;
    }

    @Override
    public StateContext<OrderStatus, OrderUpdateEvent> postTransition(StateContext<OrderStatus, OrderUpdateEvent> stateContext) {
        Transition<OrderStatus, OrderUpdateEvent> transition = stateContext.getTransition();
        State<OrderStatus, OrderUpdateEvent> source = transition.getSource();
        State<OrderStatus, OrderUpdateEvent> target = stateContext.getStateMachine().getState();
        Trigger<OrderStatus, OrderUpdateEvent> trigger = transition.getTrigger();
        OrderStatus preTransitionStatus = source.getId();
        OrderStatus postTransitionStatus = target.getId();
        if (source != null && target != null) {
            OrderUpdate orderUpdate = (OrderUpdate) stateContext.getMessageHeader(StateMachineConstants.ORDER_UPDATE);
            //do Log Activity
            //doLogActivity
            //do publish to the auditor
            //doPublish update to the client or source
            log.debug("transition ended {}:{}-->{}", trigger.getEvent(), preTransitionStatus, postTransitionStatus);
        }


        return stateContext;
    }

    @Override
    public Transition<OrderStatus, OrderUpdateEvent> transitionsConfigured(OrderStatus inputSource,
                                                                           OrderUpdateEvent inputEvent, TransitionKind inputTransitionType) throws Exception {

        StateMachine<OrderStatus, OrderUpdateEvent> stateMachine = orderStateMachineTransitionConfiguration.getStateMachine();
        Transition<OrderStatus, OrderUpdateEvent> allowedTransition = null;
        Collection<Transition<OrderStatus, OrderUpdateEvent>> transitions = stateMachine.getTransitions();
        for (Transition<OrderStatus, OrderUpdateEvent> transition : transitions) {
            OrderStatus source = transition.getSource().getId();
            OrderUpdateEvent event = transition.getTrigger().getEvent();
            TransitionKind transitionKind = transition.getKind();
            log.debug("source: {}, event:{}, kind:{}", source, event, transitionKind);
            if(inputSource.equals(source) && inputEvent.equals(event) && inputTransitionType.equals(transitionKind)) {
                allowedTransition = transition;
                break;
            }
        }
        return allowedTransition;
    }
}
