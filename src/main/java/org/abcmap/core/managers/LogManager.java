package org.abcmap.core.managers;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;

import java.io.IOException;
import java.util.logging.*;

/**
 * Log utility wrapper
 */
public class LogManager {

    public static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    /**
     * Initialize and setup loggers: console logging, file logging, ...
     *
     * @throws IOException
     */

    public static LogManager initialize() throws IOException {

        // get the root logger
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();

        // remove console logging if not in debug mode
        if (MainManager.isDebugMode() == false) {
            if (handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }
        }

        // set default log level
        rootLogger.setLevel(DEFAULT_LOG_LEVEL);

        // add file logging
        FileHandler fileTxt = new FileHandler(ConfigurationConstants.LOG_DIRECTORY + "log_%g.txt", 2097152, 5, true);
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        rootLogger.addHandler(fileTxt);

        return new LogManager();
    }

    /**
     * Return a logger instance
     *
     * @param owner
     * @return
     */
    public CustomLogger getLogger(Class<?> owner) {
        return new CustomLogger(owner);
    }

}
