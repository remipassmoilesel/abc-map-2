package org.abcmap.core.shapes;

/**
 * Exception that can be thrown on drawing errors
 */
public class ShapeBuilderException extends RuntimeException {

    public ShapeBuilderException() {
        super();
    }

    public ShapeBuilderException(String message) {
        super(message);
    }

    public ShapeBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShapeBuilderException(Throwable cause) {
        super(cause);
    }

    protected ShapeBuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
