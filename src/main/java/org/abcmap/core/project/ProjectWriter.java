package org.abcmap.core.project;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.LayerIndexDAO;
import org.abcmap.core.project.dao.ProjectMetadataDAO;
import org.abcmap.core.project.layer.Layer;
import org.abcmap.core.project.layer.LayerIndexEntry;
import org.abcmap.core.project.layer.LayerType;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Contain methods to write a project
 */
public class ProjectWriter {

    private static final CustomLogger logger = LogManager.getLogger(ProjectWriter.class);

    /**
     * Temporary name of the project database when open
     */
    public static final String TEMPORARY_NAME = "project." + ConfigurationConstants.PROJECT_EXTENSION;

    /**
     * Create a new project with a single feature layer
     *
     * @return
     */
    public Project createNew(Path tempfolder) throws IOException {

        if (Files.isDirectory(tempfolder) == false) {
            throw new IOException("Temp directory does not exist");
        }

        // create a new project
        Path path = tempfolder.resolve(TEMPORARY_NAME);

        // create a new project and initialize it
        Project project = new Project(path);
        new ProjectWriter().write(project, path);
        project.initializeGeopackage();

        // add the first layer
        project.addNewLayer("First layer", true, 0, LayerType.FEATURES);

        // set the first layer active
        project.setActiveLayer(0);

        return project;
    }

    /**
     * Write a project at specified destination. If specified destination is already a file,
     * it will be overwrite
     * <p>
     * This method do not close geopackage after writing, so you have to close it manually
     *
     * @param project
     * @param destination
     * @throws IOException
     */
    public GeoPackage write(Project project, Path destination) throws IOException {

        // delete eventual previous file
        if (Files.exists(destination)) {
            Files.delete(destination);
        }

        // create a new geopackage
        GeoPackage geopkg = new GeoPackage(destination.toFile());
        geopkg.init();

        // get database connection
        try (Connection connection = geopkg.getDataSource().getConnection()) {

            // write metadata
            ProjectMetadataDAO mtdao = new ProjectMetadataDAO(connection);
            mtdao.writeMetadata(project.getMetadataContainer());

            // write layer indexes
            writeLayerIndex(connection, project);

            // write layer contents
            for (Layer layer : project.getLayers()) {

                if (LayerType.FEATURES.equals(layer.getType())) {
                    FeatureEntry fe = new FeatureEntry();
                    geopkg.add(fe, layer.getFeatureSource(), null);
                } else {
                    logger.warning("Unknown type: " + layer.getType());
                }
            }

        } catch (Exception e) {
            throw new IOException("Error while writing project", e);
        }

        return geopkg;

    }

    public static void writeLayerIndex(Connection connection, Project project) throws IOException {
        try {
            LayerIndexDAO dao = new LayerIndexDAO(connection);
            dao.writeLayerIndex(project.getLayerIndexEntries());
        } catch (DAOException e) {
            throw new IOException(e);
        }
    }

}
