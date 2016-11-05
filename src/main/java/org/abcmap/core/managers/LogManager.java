package org.abcmap.core.managers;

import org.abcmap.LaunchError;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;

import java.io.IOException;
import java.util.logging.*;

/**
 * Log utility wrapper.
 * <p>
 * LogManager is the only anager that have to be called without use MainManager
 */
public class LogManager {

    public static boolean loggersAreInitialized = false;
    public static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    /**
     * Return a logger instance
     *
     * @param owner
     * @return
     */
    public static CustomLogger getLogger(Class<?> owner) {
        if (loggersAreInitialized == false) {
            try {
                initializeLoggers();
            } catch (IOException e) {
                LaunchError.showErrorAndDie(e);
            }
            loggersAreInitialized = true;
        }
        return new CustomLogger(owner);
    }

    /**
     * Initialize and setup loggers: console logging, file logging, ...
     *
     * @throws IOException
     */
    private static LogManager initializeLoggers() throws IOException {

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

}
