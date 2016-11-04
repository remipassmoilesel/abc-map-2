package org.abcmap.core.log;

import org.abcmap.core.managers.LogManager;

import java.util.logging.Level;

/**
 * CustomLogger wrapper
 */
public class CustomLogger {

    private final java.util.logging.Logger logger;

    public CustomLogger(Class<?> owner) {
        this.logger = java.util.logging.Logger.getLogger(owner.getName());
    }

    public void debug(String message) {
        logger.log(Level.INFO, message);
    }

    public void debug(Throwable e) {
        logger.log(Level.INFO, e, () -> {
            return e.getMessage();
        });
    }

    public void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public void warning(Throwable e) {
        logger.log(Level.INFO, e, () -> {
            return e.getMessage();
        });
    }

    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void error(Throwable e) {
        logger.log(Level.SEVERE, e, () -> {
            return e.getMessage();
        });
    }


}
