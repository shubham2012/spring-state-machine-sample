package com.statemachine.sample.transition;

import com.statemachine.sample.action.ProcessInWarehouseAction;
import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderUpdateEvent;
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

@Slf4j
@Configuration
@EnableStateMachine
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderStateMachineTransitionConfiguration
        implements ApplicationContextAware {

    private final ApplicationContext context;

    private final TaskExecutor taskExecutor;

    private final TaskScheduler taskScheduler;

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

    public void configure(StateMachineStateConfigurer<OrderStatus, OrderUpdateEvent> states) throws Exception {
        states.withStates().initial(OrderStatus.CREATED).states(EnumSet.allOf(OrderStatus.class));
    }

    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderUpdateEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(false)
                .taskExecutor(taskExecutor)
                .taskScheduler(taskScheduler)
                .listener(context.getBean(StateMachineLoggingListener.class));
    }

    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderUpdateEvent> transitions)
            throws Exception {

        transitions
                .withExternal()
                .source(OrderStatus.CREATED)
                .event(OrderUpdateEvent.RECEIVED_IN_WAREHOUSE)
                .target(OrderStatus.PROCESSING_IN_WAREHOUSE)
                .guard(context.getBean(ValidateOnHandGuard.class))
                .action(context.getBean(ProcessInWarehouseAction.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }
}
