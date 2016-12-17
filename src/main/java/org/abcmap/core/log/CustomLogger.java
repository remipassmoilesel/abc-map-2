package org.abcmap.core.log;

import java.util.logging.Level;

/**
 * CustomLogger wrapper
 */
public class CustomLogger {

    private final java.util.logging.Logger logger;

    /**
     * Use LogManager.getLogger() instead.
     */
    @Deprecated
    public CustomLogger(Class<?> owner) {
        this.logger = java.util.logging.Logger.getLogger(owner.getName());
    }

    /**
     * Log a message
     *
     * @param message
     */
    public void debug(String message) {
        logger.log(Level.INFO, message);
    }

    /**
     * Log a throwable
     *
     * @param e
     */
    public void debug(Throwable e) {
        logger.log(Level.INFO, e, () -> {
            return e.getMessage();
        });
    }

    /**
     * Log a message
     *
     * @param message
     */
    public void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    /**
     * Log a throwable
     *
     * @param e
     */
    public void warning(Throwable e) {
        logger.log(Level.INFO, e, () -> {
            return e.getMessage();
        });
    }

    /**
     * Log a message
     *
     * @param message
     */
    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    /**
     * Log a throwable
     *
     * @param e
     */
    public void error(Throwable e) {
        logger.log(Level.SEVERE, e, () -> {
            return e.getMessage();
        });
    }

    /**
     * Set log level
     *
     * @param level
     */
    public void setLevel(LogLevel level) {
        logger.setLevel(level.getInternalLevel());
    }
}
