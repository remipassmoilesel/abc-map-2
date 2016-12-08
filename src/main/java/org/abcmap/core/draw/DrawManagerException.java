package org.abcmap.core.draw;

/**
 * Created by remipassmoilesel on 16/11/16.
 */
public class DrawManagerException extends Exception {
    public DrawManagerException() {
    }

    public DrawManagerException(String message) {
        super(message);
    }

    public DrawManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DrawManagerException(Throwable cause) {
        super(cause);
    }

    public DrawManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
