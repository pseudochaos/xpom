package com.pseudochaos.xpom;

public class NoValueException extends XPomException {
    public NoValueException() {
        super();
    }

    public NoValueException(Throwable cause) {
        super(cause);
    }

    public NoValueException(String message) {
        super(message);
    }

    public NoValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
