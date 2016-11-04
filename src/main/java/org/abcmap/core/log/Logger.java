package org.abcmap.core.log;

import java.util.logging.Level;

import static jdk.nashorn.internal.objects.NativeMath.log;

/**
 * Log utility wrapper
 */
public class Logger {

    public static Logger getLogger(Class<?> owner) {
        return new Logger(owner);
    }

    private Logger(Class<?> owner) {

    }

    public void warning(String message) {
        log(Level.WARNING, message);
    }

    public void error(String message) {
        log(Level.SEVERE, message);
    }

    public void log(Level level, String message) {
        System.out.println(level + ": " + message);
    }
}
