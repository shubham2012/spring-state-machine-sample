package com.statemachine.sample.transition;

import com.statemachine.sample.action.DeliveredAction;
import com.statemachine.sample.action.NotifyCustomerAction;
import com.statemachine.sample.action.OFDAction;
import com.statemachine.sample.action.PackAction;
import com.statemachine.sample.action.ProcessByVendorAction;
import com.statemachine.sample.action.ProcessInWarehouseAction;
import com.statemachine.sample.action.ReceiveInDeliveryCenterAction;
import com.statemachine.sample.action.ShipAction;
import com.statemachine.sample.action.StampItemsToOrder;
import com.statemachine.sample.action.UpdateLastScannedLocationAction;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
import com.statemachine.sample.guard.AdminRoleGuard;
import com.statemachine.sample.guard.DeliveredValidationGuard;
import com.statemachine.sample.guard.OFDGuard;
import com.statemachine.sample.guard.ReceiveInDeliveryCenterGuard;
import com.statemachine.sample.guard.ValidateJitGuard;
import com.statemachine.sample.guard.ValidateOnHandGuard;
import com.statemachine.sample.logging.StateMachineLoggingListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.statemachine.sample.constants.OrderStatus.*;

@Slf4j
@Configuration
@EnableStateMachine
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderStateMachineTransitionConfiguration
        implements ApplicationContextAware {

    private final ApplicationContext context;

    private final TaskExecutor taskExecutor;

    private final TaskScheduler taskScheduler;

    private static final OrderStatus[] ALLOWED_CANCELLATION_FROM = {CREATED, PROCESSING, PACKED};

    /**
     * Get Status machine will return the complete state machine with all the compiled states
     * @return
     * @throws Exception
     */
    public StateMachine<OrderStatus, OrderUpdateEvent> getStateMachine() throws Exception{
        StateMachineBuilder.Builder<OrderStatus, OrderUpdateEvent> builder = StateMachineBuilder.builder();
        builder.configureConfiguration().withConfiguration().beanFactory(context.getAutowireCapableBeanFactory());
        StateMachineStateConfigurer<OrderStatus, OrderUpdateEvent> states = builder.configureStates();
        configure(states);
        StateMachineTransitionConfigurer<OrderStatus, OrderUpdateEvent> transitions = builder.configureTransitions();
        configure(transitions);
        StateMachineConfigurationConfigurer<OrderStatus, OrderUpdateEvent> config = builder.configureConfiguration();
        configure(config);
        return builder.build();
    }

    /**
     * Configure and register all the states available
     *
     * @param states
     * @throws Exception
     */
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderUpdateEvent> states) throws Exception {
        states.withStates().initial(CREATED).states(EnumSet.allOf(OrderStatus.class));
    }

    /**
     * Add the state machine intermediate logger and any other configurer listeners if any available or you want to
     * @param config
     * @throws Exception
     */
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderUpdateEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(false)
                .taskExecutor(taskExecutor)
                .taskScheduler(taskScheduler)
                .listener(context.getBean(StateMachineLoggingListener.class));
    }

    /**
     * Configure the state transitions
     *
     * @param transitions
     * @throws Exception
     */
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderUpdateEvent> transitions)
            throws Exception {

        transitions
                .withExternal()
                .source(CREATED)
                .event(OrderUpdateEvent.PROCESS_IN_WAREHOUSE)
                .target(PROCESSING)
                .guard(context.getBean(ValidateOnHandGuard.class))
                .action(context.getBean(ProcessInWarehouseAction.class))
                .and()
                .withExternal()
                .source(CREATED)
                .event(OrderUpdateEvent.DISPATCH_BY_VENDOR)
                .target(PROCESSING)
                .guard(context.getBean(ValidateJitGuard.class))
                .action(context.getBean(ProcessByVendorAction.class))
                .and()
                .withExternal()
                .source(PROCESSING)
                .event(OrderUpdateEvent.PACK)
                .target(PACKED)
                .action(context.getBean(PackAction.class))
                .action(context.getBean(NotifyCustomerAction.class))
                .and()
                .withExternal()
                .source(PACKED)
                .event(OrderUpdateEvent.SHIP)
                .target(SHIPPED)
                .action(context.getBean(ShipAction.class))
                .action(context.getBean(NotifyCustomerAction.class))
                .and()
                .withExternal()
                .source(SHIPPED)
                .event(OrderUpdateEvent.HAND_OVER_TO_DELIVERY_CENTER)
                .target(RECEIVED_IN_DELIVERY_CENTER)
                .guard(context.getBean(ReceiveInDeliveryCenterGuard.class))
                .action(context.getBean(ReceiveInDeliveryCenterAction.class))
                .and()
                .withExternal()
                .source(RECEIVED_IN_DELIVERY_CENTER)
                .event(OrderUpdateEvent.PICKED_UP_BY_DELIVERY_EXECUTIVE)
                .target(OUT_FOR_DELIVERY)
                .guard(context.getBean(OFDGuard.class))
                .action(context.getBean(OFDAction.class))
                .action(context.getBean(NotifyCustomerAction.class))
                .and()
                .withExternal()
                .source(OUT_FOR_DELIVERY)
                .event(OrderUpdateEvent.DELIVERED)
                .target(DELIVERED)
                .guard(context.getBean(DeliveredValidationGuard.class))
                .action(context.getBean(DeliveredAction.class))
                .action(context.getBean(NotifyCustomerAction.class))
                .source(SHIPPED)
                .event(OrderUpdateEvent.DELIVERED)
                .target(DELIVERED)
                .guard(context.getBean(AdminRoleGuard.class))
                .action(context.getBean(DeliveredAction.class))
                .action(context.getBean(NotifyCustomerAction.class));

        // With Internal is used where your status doesn't change but still
        // you need to update the intermediate small updates to system and client
        transitions
                .withInternal()
                .source(PROCESSING)
                .event(OrderUpdateEvent.READY_TO_PACK)
                .guard(context.getBean(ValidateOnHandGuard.class))
                .action(context.getBean(StampItemsToOrder.class))
                .and()
                .withInternal()
                .source(SHIPPED)
                .event(OrderUpdateEvent.IN_TRANSIT_SCAN)
                .action(context.getBean(UpdateLastScannedLocationAction.class))
                .and().withInternal()
                .source(DELIVERED)
                .event(OrderUpdateEvent.DELIVERED); // In this case, simply handle idempotency

        // In case where you have same target from multiple sources
        for (OrderStatus orderStatus : ALLOWED_CANCELLATION_FROM) {
            transitions.withExternal()
                    .source(orderStatus)
                    .event(OrderUpdateEvent.CANCEL)
                    .target(CANCELLED)
                    .action(context.getBean(NotifyCustomerAction.class));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

}
