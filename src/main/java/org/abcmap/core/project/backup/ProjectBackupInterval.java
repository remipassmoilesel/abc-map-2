package org.abcmap.core.project.backup;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.ProjectWriter;
import org.abcmap.core.threads.ThreadManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

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
