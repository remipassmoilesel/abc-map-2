package org.abcmap.core.log;

import java.util.logging.Level;

/**
 * Wrapper for logging level
 */
public enum LogLevel {

    DEBUG(Level.INFO), WARNING(Level.WARNING), ERROR(Level.SEVERE);

    private final Level level;

    LogLevel(Level level) {
        this.level = level;
    }

    public Level getInternalLevel() {
        return level;
    }
}