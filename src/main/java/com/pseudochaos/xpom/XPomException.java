package com.pseudochaos.xpom;

public class XPomException extends RuntimeException {
    public XPomException() {
        super();
    }

    public XPomException(String message) {
        super(message);
    }

    public XPomException(String message, Throwable cause) {
        super(message, cause);
    }

    public XPomException(Throwable cause) {
        super(cause);
    }
}
