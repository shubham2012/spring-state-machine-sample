package com.statemachine.sample.exception;

import java.text.MessageFormat;

public class GuardHackException extends RuntimeException {

    private static final long serialVersionUID = -2372687214630918223L;

    public GuardHackException(String message) {
        super(message);
    }

    public GuardHackException(String message, String... paramArray) {
        super(MessageFormat.format(message, (Object[]) paramArray));
    }
}
