package org.abcmap.core.project;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.threads.ThreadManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple utility to save a database copy at specified interval
 */
public class ProjectBackupInterval extends TimerTask{

    private static final CustomLogger logger = LogManager.getLogger(ProjectBackupInterval.class);
    private static final String BACKUP_SUFFIX = ".backup";
    private final ReentrantLock savingLock;
    private final int intervalMs;
    private ProjectManager pman;
    private Timer timer;

    public ProjectBackupInterval(int intervalMs) {
        this.savingLock = new ReentrantLock();
        this.intervalMs = intervalMs;
    }

    public void start(){

        stop();

        timer = new Timer();
        timer.schedule(this, 0, intervalMs);
    }

    public void stop() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void run() {

        if(pman == null){
            this.pman = MainManager.getProjectManager();
        }

        ThreadManager.runLater(()->{

            if(savingLock.tryLock() == false){
                logger.debug("Timer already saving, abort");
                return;
            }

            // save project next to the original but with a backup suffix
            ProjectWriter writer = new ProjectWriter();
            Project p = pman.getProject();
            Path backupPath =  p.getDatabasePath().resolveSibling(p.getDatabasePath().getFileName() + BACKUP_SUFFIX);
            try {
                writer.write(p, backupPath);
            } catch (IOException e) {
                logger.error("Unable to backup project");
                logger.error(e);
            }

            savingLock.unlock();

        });

    }
}
