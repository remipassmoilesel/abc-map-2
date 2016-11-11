package org.abcmap.core.shapes;

/**
 * Exception that can be thrown on drawing errors
 */
public class DrawingException extends RuntimeException {
    public DrawingException() {
        super();
    }

    public DrawingException(String message) {
        super(message);
    }

    public DrawingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DrawingException(Throwable cause) {
        super(cause);
    }

    protected DrawingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
