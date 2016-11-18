package org.abcmap.core.project;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.dao.LayerIndexDAO;
import org.abcmap.core.project.dao.ProjectMetadataDAO;
import org.abcmap.core.project.dao.StyleDAO;
import org.abcmap.core.project.layer.FeatureLayer;
import org.abcmap.core.project.layer.LayerIndexEntry;
import org.abcmap.core.project.layer.LayerType;
import org.abcmap.core.utils.SQLiteUtils;
import org.geotools.jdbc.JDBCDataStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Read a project and store information
 */
public class ProjectReader {

    private static final CustomLogger logger = LogManager.getLogger(ProjectReader.class);

    /**
     * Read the specified project file and return a Project object.
     * <p>
     * A copy of the project file is created in the specified temp directory.
     *
     * @param tempfolder
     * @param projectFile
     * @return
     * @throws IOException
     */
    public Project read(Path tempfolder, Path projectFile) throws IOException {

        // copy file in temp directory
        Path newTempDatabase = tempfolder.resolve(ProjectWriter.TEMPORARY_NAME);
        Files.copy(projectFile, newTempDatabase);

        // create project
        Project project = new Project(newTempDatabase);
        project.initializeGeopackage();

        // get database connection with project
        JDBCDataStore datastore = SQLiteUtils.getDatastoreFromGeopackage(newTempDatabase);

        try {

            // get layer index
            LayerIndexDAO lidao = new LayerIndexDAO(newTempDatabase);
            ArrayList<LayerIndexEntry> indexes = lidao.readAllEntries();

            // create layers
            for (LayerIndexEntry entry : indexes) {

                if (LayerType.FEATURES.equals(entry.getType())) {
                    // here features from a shapefile should be named with the layer id
                    FeatureLayer layer = new FeatureLayer(entry, project.getGeopkg(), false);
                    project.addLayer(layer);
                } else {
                    logger.warning("Unknown type: " + entry.getType());
                }

            }

            // set the first layer active
            project.setActiveLayer(0);

            //TODO read layouts

            // get metadata
            ProjectMetadataDAO mtdao = new ProjectMetadataDAO(projectFile);
            project.setMetadataContainer(mtdao.readMetadata());

            // get styles
            StyleDAO stdao = new StyleDAO(projectFile);
            project.getStyleLibrary().setStyleCollection(stdao.readStyles());

        } catch (Exception e) {
            throw new IOException("Error while reading project", e);
        }

        return project;
    }
}
