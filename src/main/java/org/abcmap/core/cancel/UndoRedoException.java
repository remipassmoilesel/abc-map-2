package org.abcmap.core.cancel;

/**
 * Created by remipassmoilesel on 18/01/17.
 */
public class UndoRedoException extends Exception{

    public UndoRedoException() {
    }

    public UndoRedoException(String message) {
        super(message);
    }

    public UndoRedoException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndoRedoException(Throwable cause) {
        super(cause);
    }

    public UndoRedoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
