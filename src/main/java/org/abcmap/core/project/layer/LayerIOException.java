package org.abcmap.core.project.layer;

/**
 * Lighter exception thrown when IO errors occurs on layers.
 * <p>
 * Allow to not handle exceptions when using local database.
 */
public class LayerIOException extends RuntimeException {

    public LayerIOException() {
        super();
    }

    public LayerIOException(String message) {
        super(message);
    }

    public LayerIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public LayerIOException(Throwable cause) {
        super(cause);
    }

    protected LayerIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
