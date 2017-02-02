package org.abcmap.core.managers;

import org.abcmap.LaunchError;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

/**
 * Log utility wrapper.
 * <p>
 * LogManager is the only anager that have to be called without use MainManager
 */
public class LogManager {

    // example:
    // private static final CustomLogger logger = LogManager.getLogger(Project.class);

    private static LogManager instance;
    private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;
    private static Path LOG_ROOT = ConfigurationConstants.LOG_DIRECTORY;

    /**
     * Return a logger instance
     *
     * @param owner
     * @return
     */
    public static CustomLogger getLogger(Class<?> owner) {
        if (instance == null) {
            try {
                instance = initializeLoggers();
            } catch (IOException e) {
                LaunchError.showErrorAndDie(e);
            }
        }
        return instance.createLogger(owner);
    }

    /**
     * Initialize and setup loggers: console logging, file logging, ...
     *
     * @throws IOException
     */
    private static LogManager initializeLoggers() throws IOException {

        if (Files.isDirectory(LOG_ROOT) == false) {
            Files.createDirectories(LOG_ROOT);
        }

        // get the root logger
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");

        // set default log level
        rootLogger.setLevel(DEFAULT_LOG_LEVEL);

        // add file logging
        FileHandler fileTxt = new FileHandler(LOG_ROOT + File.separator + "log_%g.txt", 2097152, 5, true);
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        rootLogger.addHandler(fileTxt);

        return new LogManager();
    }

    private CustomLogger createLogger(Class<?> owner) {
        return new CustomLogger(owner);
    }

}
