package org.abcmap.core.managers;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.ProjectReader;
import org.abcmap.core.project.ProjectWriter;
import org.abcmap.core.project.backup.ProjectBackupInterval;
import org.abcmap.core.project.layer.TileLayer;
import org.abcmap.gui.utils.GuiUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Here are managed all operations concerning projects
 */
public class ProjectManager implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(ProjectManager.class);
    private final ProjectBackupInterval backupTimer;
    private final EventNotificationManager notificationManager;
    private Project currentProject;
    private final TempFilesManager tempMan;

    public ProjectManager() {

        this.currentProject = null;
        tempMan = MainManager.getTempFilesManager();

        notificationManager = new EventNotificationManager(ProjectManager.this);

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
    public void closeProject() throws IOException {

        GuiUtils.throwIfOnEDT();

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

        GuiUtils.throwIfOnEDT();

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

        GuiUtils.throwIfOnEDT();

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

        GuiUtils.throwIfOnEDT();

        ProjectWriter writer = new ProjectWriter();
        Path tempDir = tempMan.createTempDirectory(currentProject.getTempDirectory());
        try {
            writer.export(currentProject, p);
        } catch (IOException e) {
            throw new IOException("Error while writing project", e);
        }

    }

    /**
     * Create a fake project for debug purposes
     */
    public void createFakeProject() throws IOException {

        GuiUtils.throwIfOnEDT();

        String root = "/tiles/osm_";

        createNewProject();

        Project project = getProject();

        // create a tile layer
        TileLayer layer = (TileLayer) project.addNewTileLayer("Tile layer 1", true, 0);

        // TODO: get more realistic coordinates
        ArrayList<Coordinate> positions = new ArrayList();
        positions.add(new Coordinate(2000, 1000));
        positions.add(new Coordinate(1478.6803359985352, 982.1077270507812));
        positions.add(new Coordinate(919.2462692260742, 1257.8359985351562));
        positions.add(new Coordinate(919.2940902709961, 1032.83740234375));

        int totalToInsert = 4;
        for (int i = 0; i < totalToInsert; i++) {

            String imgPath = root + (i + 1) + ".png";

            InputStream res = ProjectManager.class.getResourceAsStream(imgPath);
            if (res == null) {
                throw new IOException("Image is null: " + imgPath);
            }

            BufferedImage img = ImageIO.read(res);

            layer.addTile(img, positions.get(i));
        }

        layer.refreshCoverage();
    }

    public Project getProject() {
        return currentProject;
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notificationManager;
    }
}
