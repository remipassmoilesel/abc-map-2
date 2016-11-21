package org.abcmap.core.managers;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.backup.ProjectBackupInterval;
import org.abcmap.core.project.ProjectReader;
import org.abcmap.core.project.ProjectWriter;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Here are managed all operations around projects
 */
public class ProjectManager {

    private static final CustomLogger logger = LogManager.getLogger(ProjectManager.class);
    private final ProjectBackupInterval backupTimer;
    private Project currentProject;
    private final TempFilesManager tempMan;

    public ProjectManager() {

        this.currentProject = null;
        tempMan = MainManager.getTempFilesManager();

        backupTimer = new ProjectBackupInterval(ConfigurationConstants.BACKUP_INTERVAL);
    }

    /**
     * Return true if a project is loaded and available
     *
     * @return
     */
    public boolean isInitialized() {
        return currentProject != null;
    }

    /**
     * Create a new project and load it
     *
     * @throws IOException
     */
    public Project createNewProject() throws IOException {

        // get a new temp path
        Path dir = null;
        try {
            dir = tempMan.createProjectTempDirectory();
        } catch (IOException e) {
            throw new IOException("Error while creating temp directory", e);
        }

        ProjectWriter writer = new ProjectWriter();

        // test creation
        try {
            currentProject = writer.createNew(dir);
        } catch (IOException e) {
            throw new IOException("Error while creating project", e);
        }

        return currentProject;

    }

    private void startBackupInterval() {
        backupTimer.stop();
        backupTimer.start();
    }

    private void stopBackupInterval() {
        backupTimer.stop();
    }

    /**
     * Close the current project and delete temprorary files
     */
    public void closeProjet() throws IOException {

        if (isInitialized() == false) {
            logger.debug("Cannot close project, it was not initialized.");
            return;
        }

        // close resources
        currentProject.close();

        // delete files
        try {
            tempMan.deleteTempFile(currentProject.getTempDirectory());
        } catch (IOException e) {
            throw new IOException("Error while deleting temp files", e);
        }

    }

    /**
     * Load a project
     *
     * @param p
     */
    public void openProject(Path p) throws IOException {

        // get a new temp path
        Path dir = null;
        try {
            dir = tempMan.createProjectTempDirectory();
        } catch (IOException e) {
            throw new IOException("Error while creating temp directory", e);
        }

        ProjectReader reader = new ProjectReader();
        try {
            currentProject = reader.read(dir, p);
        } catch (IOException e) {
            throw new IOException("Error while reading project", e);
        }

    }

    /**
     * Save the project to the final location.
     */
    public void saveProject() throws IOException {

        if (currentProject.getFinalPath() == null) {
            throw new IOException("Final path of project is null");
        }

        saveProject(currentProject.getFinalPath());

    }

    /**
     * Write the current project at specified location
     *
     * @param p
     * @throws IOException
     */
    public void saveProject(Path p) throws IOException {

        ProjectWriter writer = new ProjectWriter();
        Path tempDir = tempMan.createTempDirectory(currentProject.getTempDirectory());
        try {
            writer.export(currentProject, p);
        } catch (IOException e) {
            throw new IOException("Error while writing project", e);
        }

    }

    public Project getProject() {
        return currentProject;
    }

}
