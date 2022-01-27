package com.statemachine.sample.exception;

public class StateMachineActionException extends RuntimeException {

    public StateMachineActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateMachineActionException(String message) {
        super(message);
    }

    public StateMachineActionException(Throwable cause) {
        super(cause);
    }

}
