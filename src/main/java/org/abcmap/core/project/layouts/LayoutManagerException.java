package org.abcmap.core.project.layouts;

/**
 * Exception thrown by Layout manager
 */
public class LayoutManagerException extends Exception {

    public static final String ALREADY_PRINTING = "ALREADY_PRINTING";
    public static final String PROJECT_NON_INITIALIZED = "PROJECT_NON_INITIALIZED";

    public LayoutManagerException() {
    }

    public LayoutManagerException(String message) {
        super(message);
    }

    public LayoutManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LayoutManagerException(Throwable cause) {
        super(cause);
    }

    public LayoutManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Return true if specified exception is thrown because another print operation is in progress
     *
     * @param e
     * @return
     */
    public static boolean isAlreadyPrinting(Exception e) {
        if (e instanceof LayoutManagerException == false) {
            return false;
        }

        if (e.getMessage().equals(ALREADY_PRINTING) == false) {
            return false;
        }

        return true;
    }
}
