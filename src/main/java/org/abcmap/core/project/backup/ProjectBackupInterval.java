package org.abcmap.core.project.backup;

import java.util.Timer;

/**
 * A simple utility to save a database copy at specified interval
 */
public class ProjectBackupInterval {

    private final int intervalMs;
    private Timer timer;

    public ProjectBackupInterval(int intervalMs) {
        this.intervalMs = intervalMs;
    }

    public void start() {

        stop();

        timer = new Timer();
        timer.schedule(new ProjectBackupTask(intervalMs), 0, intervalMs);

    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
