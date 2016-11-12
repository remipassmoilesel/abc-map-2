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
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import static org.geotools.xml.DocumentWriter.logger;

/**
 * Created by remipassmoilesel on 12/11/16.
 */
public class ProjectBackupTask extends TimerTask{

    private static final CustomLogger logger = LogManager.getLogger(ProjectBackupTask.class);

    private ProjectManager pman;
    private static final String BACKUP_SUFFIX = ".backup";
    private final ReentrantLock savingLock;

    public ProjectBackupTask(int intervalMs) {
        this.savingLock = new ReentrantLock();
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
