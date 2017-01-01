package org.abcmap.core.managers;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.ProjectReader;
import org.abcmap.core.project.ProjectWriter;
import org.abcmap.core.project.backup.ProjectBackupInterval;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.project.layers.TileLayer;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.PrimitiveIterator;
import java.util.Random;

/**
 * Here are managed all operations concerning projects
 */
public class ProjectManager implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(ProjectManager.class);
    private static final String FAKE_PROJECT_1 = "--create-fake-project";
    private static final String FAKE_PROJECT_2 = "--create-fake-project2";
    private final ProjectBackupInterval backupTimer;
    private final EventNotificationManager notifm;
    private Project currentProject;
    private final TempFilesManager tempMan;

    public ProjectManager() {

        this.currentProject = null;
        tempMan = MainManager.getTempFilesManager();

        notifm = new EventNotificationManager(ProjectManager.this);

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

        notifm.fireEvent(new ProjectEvent(ProjectEvent.NEW_PROJECT_LOADED));

        return currentProject;

    }

    /**
     * Start project backup interval, to save project a regular interval
     */
    private void startBackupInterval() {
        backupTimer.stop();
        backupTimer.start();
    }

    /**
     * Stop project backup interval
     */
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

        notifm.fireEvent(new ProjectEvent(ProjectEvent.PROJECT_CLOSED));

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

        notifm.fireEvent(new ProjectEvent(ProjectEvent.NEW_PROJECT_LOADED));
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
        try {
            writer.export(currentProject, p);
        } catch (IOException e) {
            throw new IOException("Error while writing project", e);
        }

    }

    /**
     * Create a fake project for debug purposes
     */
    public void createFakeProject(String id) throws IOException {

        GuiUtils.throwIfOnEDT();

        createNewProject();
        Project fakeProject = getProject();

        if (id == null || id.equals(FAKE_PROJECT_1)) {

            // create a tile layer
            TileLayer tileLayer = (TileLayer) fakeProject.addNewTileLayer("Tile layer 1", true, 0);

            String imgPath = "/tiles/osm_large.png";
            InputStream res = ProjectManager.class.getResourceAsStream(imgPath);
            if (res == null) {
                throw new IOException("Image is null: " + imgPath);
            }

            BufferedImage img = ImageIO.read(res);
            tileLayer.addTile(img, new Coordinate(45.60443, 4.082794));
            tileLayer.refreshCoverage();

            // move layer tile under other layers
            fakeProject.moveLayerToIndex(tileLayer, 0);

            fakeProject.setActiveLayer(1);

            // populate with random features
            int featureNumber = 10;
            ReferencedEnvelope bounds = tileLayer.getBounds();
            PrimitiveIterator.OfDouble rand = new Random().doubles(bounds.getMinX(), bounds.getMaxX()).iterator();
            LineBuilder builder = new LineBuilder((org.abcmap.core.project.layers.FeatureLayer) fakeProject.getActiveLayer(), MainManager.getDrawManager().getActiveStyle());
            for (int i = 0; i < featureNumber; i++) {
                builder.newLine(new Coordinate(rand.next(), rand.next()));
                for (int j = 0; j < 5; j++) {
                    builder.addPoint(new Coordinate(rand.next(), rand.next()));
                }
                builder.terminateLine(new Coordinate(rand.next(), rand.next()));
            }
        }

        // create a project with a single shape file
        else if (id.equals(FAKE_PROJECT_2)) {
            fakeProject.addNewShapeFileLayer(Paths.get("data/france-communes/communes-20160119.shp"));
            fakeProject.addNewShapeFileLayer(Paths.get("data/france-communes-ed50/france-communes-ed50.shp"));
            fakeProject.addNewShapeFileLayer(Paths.get("data/cinema/les_salles_de_cinemas_en_ile-de-france.shp"));
        }

        // invalid argument
        else {
            throw new IllegalArgumentException("Invalid project id: " + id);
        }

        // second notification sent
        notifm.fireEvent(new ProjectEvent(ProjectEvent.NEW_PROJECT_LOADED));
    }

    /**
     * Fire an event meaning that layouts sheets changed
     */
    public void fireLayoutListChanged() {
        notifm.fireEvent(new ProjectEvent(ProjectEvent.LAYOUTS_LIST_CHANGED, null));
    }

    /**
     * Fire an event meaning that layers list changed
     */
    public void fireLayerListChanged() {
        notifm.fireEvent(new ProjectEvent(ProjectEvent.LAYERS_LIST_CHANGED, null));
    }

    /**
     * @param lay
     */
    public void fireLayerChanged(AbstractLayer lay) {
        notifm.fireEvent(new ProjectEvent(ProjectEvent.LAYER_CHANGED, lay));
    }

    /**
     * Return current project loaded. Can return null.
     *
     * @return
     */
    public Project getProject() {
        return currentProject;
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}
