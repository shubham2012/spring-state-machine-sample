package com.statemachine.sample.constants;

import java.text.MessageFormat;

public enum MessageConstant {
    TRANSITION("transition {0} -> {1} -> {2}"),
    TRANSITION_STARTED("Transition started for states: {0} -> {1} -> {2}"),
    TRANSITION_ENDED("Transition ended for states: {0} -> {1} -> {2}"),
    STATE_MACHINE_STARTED("State machine started: {0}"),
    STATE_MACHINE_STOPPED("State machine stopped: {0}"),
    STATE_MACHINE_ERROR("State machine: {0} has error: {1}"),
    STATE_EXISTED("State Existed {0}"),
    STATE_ENTERED("Entered in state {0}"),
    STATE_CHANGED("State changed from {0} to {1}"),
    EVENT_NOT_ACCEPTED("Event not accepted {0} {1} {2}"),
    ERROR_DURING_EXECUTION("Error during state machine execution "),
    ORDER_NOT_FOUND("Order not found with id: {0}");

    public final String value;

    MessageConstant(String value) {
        this.value = value;
    }

    public String get(Object... args) {
        return new MessageFormat(this.value).format(args);
    }
}
