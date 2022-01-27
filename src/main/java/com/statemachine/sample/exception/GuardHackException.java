package com.statemachine.sample.exception;

import java.text.MessageFormat;

public class GuardHackException extends RuntimeException {

    public GuardHackException(String message) {
        super(message);
    }

    public GuardHackException(String message, String... paramArray) {
        super(MessageFormat.format(message, (Object[]) paramArray));
    }
}
