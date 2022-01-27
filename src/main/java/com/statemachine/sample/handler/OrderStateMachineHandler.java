package com.statemachine.sample.handler;

import com.statemachine.sample.constants.EventStatus;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.constants.OrderUpdateResponseCode;
import com.statemachine.sample.constants.StateMachineConstants;
import com.statemachine.sample.domain.ActivityEntry;
import com.statemachine.sample.domain.OrderEntry;
import com.statemachine.sample.domain.OrderUpdate;
import com.statemachine.sample.domain.OrderUpdateResponse;
import com.statemachine.sample.exception.GuardHackException;
import com.statemachine.sample.service.ActivityLoggingService;
import com.statemachine.sample.service.OrderService;
import com.statemachine.sample.transition.OrderStateMachineTransitionConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.statemachine.sample.constants.OrderUpdateResponseCode.SUCCESS;
import static com.statemachine.sample.constants.OrderUpdateResponseCode.TRANSITION_NOT_ALLOWED;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderStateMachineHandler extends StateMachineInterceptorAdapter<OrderStatus, OrderUpdateEvent>
        implements StateMachineHandler {

    private final OrderStateMachineTransitionConfiguration orderStateMachineTransitionConfiguration;

    private final OrderService orderService;

    private final ActivityLoggingService activityLoggingService;

    /**
     *
     * @param orderUpdate
     * @return
     * @throws Exception
     */
    @Override
    public OrderUpdateResponse handleEvent(OrderUpdate orderUpdate) {
        OrderEntry order = orderService.getById(orderUpdate.getOrderId());
        if (Objects.isNull(order)) {
            return buildResponse(new OrderUpdateResponse(), OrderUpdateResponseCode.ORDER_NOT_FOUND, "Order Not found");
        }
        OrderUpdateResponse orderUpdateResponse = invokeStateMachine(orderUpdate, orderUpdate.getEvent(), order);
        return orderUpdateResponse;
    }

    /**
     * Transitions Configured can be used to find out if the transition is allowed for a particular event
     * @param inputSource
     * @param inputEvent
     * @param inputTransitionType
     * @return
     * @throws Exception
     */
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

    /**
     * Invoking state machine for the transition
     * @param orderUpdate
     * @param event
     * @param orderEntry
     * @return
     */
    private OrderUpdateResponse invokeStateMachine(OrderUpdate orderUpdate, OrderUpdateEvent event, OrderEntry orderEntry) {
        OrderUpdateResponse response = new OrderUpdateResponse();
        response.setOrderStatusPreEvent(orderEntry.getStatus());
        try {
            StateMachine<OrderStatus, OrderUpdateEvent> stateMachine =
                    loadStateMachineWithState(orderEntry.getStatus());

            Message<OrderUpdateEvent> message = MessageBuilder.withPayload(event)
                    .setHeader(StateMachineConstants.ORDER_UPDATE, orderUpdate)
                    .setHeader(StateMachineConstants.ORDER, orderEntry)
                    .build();
            boolean stateTransition = stateMachine.sendEvent(message);

            if (stateTransition) {
                buildResponse(response, SUCCESS, orderUpdate);
                response.setOrderStatusPostEvent(stateMachine.getState().getId());
            } else {
                String errorMessage = new MessageFormat("{0} not defined for orderId: {1}, and status: {2}")
                        .format(String.valueOf(event), orderEntry.getId(), orderEntry.getStatus());
                log.warn(errorMessage);
                buildResponse(response, TRANSITION_NOT_ALLOWED, errorMessage);
            }
        } catch (GuardHackException ex) {
            log.warn("Transition not allowed for order {}", orderEntry.getId(), ex);
            buildResponse(response, TRANSITION_NOT_ALLOWED,
                    new MessageFormat("Transition not allowed for order {0}").format(orderEntry.getId()));
        } catch (Exception ex) {
            log.error("Transition error occurred for order {}", orderEntry.getId(), ex);
            buildResponse(response, OrderUpdateResponseCode.ERROR_DURING_TRANSITION,
                    new MessageFormat("Transition error occurred for order {}").format( orderEntry.getId()));
        } finally {
            ActivityEntry activity = buildActivity(orderUpdate, response);
            activityLoggingService.addActivity(activity);
        }
        return response;
    }

    /**
     * Load state machine
     * @param orderStatus
     * @return
     * @throws Exception
     */
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

    /**
     *
     * @param stateContext
     * @return
     */
    @Override
    public StateContext<OrderStatus, OrderUpdateEvent> postTransition(StateContext<OrderStatus, OrderUpdateEvent> stateContext) {
        Transition<OrderStatus, OrderUpdateEvent> transition = stateContext.getTransition();
        State<OrderStatus, OrderUpdateEvent> source = transition.getSource();
        State<OrderStatus, OrderUpdateEvent> target = stateContext.getStateMachine().getState();
        Trigger<OrderStatus, OrderUpdateEvent> trigger = transition.getTrigger();
        OrderStatus preTransitionStatus = source.getId();
        OrderStatus postTransitionStatus = target.getId();
        if (Objects.nonNull(source) && Objects.nonNull(target)) {
            OrderUpdate orderUpdate = (OrderUpdate) stateContext.getMessageHeader(StateMachineConstants.ORDER_UPDATE);
            OrderEntry order = (OrderEntry) stateContext.getMessageHeader(StateMachineConstants.ORDER);

            doBroadcastEventToQueue(orderUpdate, order);
            pushUpdateToClient(orderUpdate, order);
            pushUpdateToAuditor(orderUpdate, order);
            log.debug("transition ended {}:{}-->{}", trigger.getEvent(), preTransitionStatus, postTransitionStatus);
        }


        return stateContext;
    }

    /**
     *
     * @param orderUpdate
     * @param order
     */
    private void pushUpdateToAuditor(OrderUpdate orderUpdate, OrderEntry order) {
        // push update to auditor if any
    }

    /**
     *
     * @param orderUpdate
     * @param order
     */
    private void pushUpdateToClient(OrderUpdate orderUpdate, OrderEntry order) {
        //based on client, push the respective update to client
    }

    /**
     *
     * @param orderUpdate
     * @param order
     */
    private void doBroadcastEventToQueue(OrderUpdate orderUpdate, OrderEntry order) {
        // Broadcast the event to the queue for internal systems to use
        // For E.g.: Elastic search if any, Other services who act of different event types of order
    }


    //---------------------- builders -----------------------------

    /**
     *
     * @param response
     * @param code
     * @param message
     * @return
     */
    private OrderUpdateResponse buildResponse(OrderUpdateResponse response, OrderUpdateResponseCode code, String message){
        response.setOrderUpdateResponseCode(code);
        response.setExceptionMessage(message);
        return response;
    }

    /**
     *
     * @param response
     * @param code
     * @param orderUpdate
     * @return
     */
    private OrderUpdateResponse buildResponse(OrderUpdateResponse response, OrderUpdateResponseCode code, OrderUpdate orderUpdate){
        response.setOrderUpdateResponseCode(code);
        response.setOrderUpdate(orderUpdate);
        return response;
    }

    /**
     *
     * @param orderupdate
     * @param orderUpdateResponse
     * @return
     */
    private ActivityEntry buildActivity(OrderUpdate orderupdate, OrderUpdateResponse orderUpdateResponse){
        ActivityEntry entry = new ActivityEntry();
        entry.setOrderId(orderupdate.getOrderId());
        entry.setEvent(orderupdate.getEvent());
        entry.setFromStatus(orderUpdateResponse.getOrderStatusPreEvent());
        entry.setToStatus(orderUpdateResponse.getOrderStatusPostEvent());

        switch (orderUpdateResponse.getOrderUpdateResponseCode()) {
            case SUCCESS:
                entry.setRemarks(orderupdate.getRemarks());
                entry.setEventStatus(EventStatus.SUCCESS);
                break;
            case TRANSITION_NOT_CONFIGURED:
            case TRANSITION_NOT_ALLOWED:
            case ERROR_DURING_TRANSITION:
                entry.setRemarks(orderUpdateResponse.getExceptionMessage());
                entry.setEventStatus(EventStatus.FAILED);
                break;
            default:
                break;
        }
        return entry;
    }
}
