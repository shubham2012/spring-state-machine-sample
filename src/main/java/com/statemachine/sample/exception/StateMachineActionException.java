package com.statemachine.sample.exception;

public class StateMachineActionException extends RuntimeException {

    private static final long serialVersionUID = 4014531369359325391L;

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
