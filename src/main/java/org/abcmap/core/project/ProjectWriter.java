package org.abcmap.core.project;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.TempFilesManager;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.LayerIndexDAO;
import org.abcmap.core.project.dao.ProjectMetadataDAO;
import org.abcmap.core.project.dao.StyleDAO;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.core.project.layer.FeatureLayer;
import org.abcmap.core.project.layer.LayerType;
import org.abcmap.core.utils.SQLUtils;
import org.abcmap.core.utils.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.jdbc.JDBCDataStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contain methods to write a project
 */
public class ProjectWriter {

    private static final CustomLogger logger = LogManager.getLogger(ProjectWriter.class);

    /**
     * Temporary name of the project database when open
     */
    public static final String PROJECT_TEMP_NAME = "project." + ConfigurationConstants.PROJECT_EXTENSION + ".tmp";
    private final TempFilesManager tempMan;

    public ProjectWriter() {
        tempMan = MainManager.getTempFilesManager();
    }

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
        Path path = tempfolder.resolve(PROJECT_TEMP_NAME);

        // create a new project and initialize it
        Project project = new Project(path);

        ProjectWriter pwriter = new ProjectWriter();
        pwriter.write(project, path, false, null);

        project.initializeDatabase();

        // add the first layer
        project.addNewFeatureLayer("First layer", true, 0);

        // set the first layer active
        project.setActiveLayer(0);

        return project;
    }


    /**
     * Write a project at specified destination. If specified destination is already a file,
     * it will be overwrite
     * <p>
     * If bundle is set to true, you have to set a temporary directory. Database will be compressed in one file, and tempdir will be deleted just after.
     *
     * @param project
     * @param destination
     * @return
     * @throws IOException
     */
    public boolean write(Project project, Path destination) throws IOException {
        return write(project, destination, true, tempMan.createTempDirectory(project.getTempDirectory()));
    }

    /**
     * Write a project at specified destination. If specified destination is already a file,
     * it will be overwrite
     * <p>
     * If bundle is set to true, you have to set a temporary directory. Database will be compressed in one file, and tempdir will be deleted just after.
     *
     * @param project
     * @param destination
     * @throws IOException
     */
    public boolean write(Project project, Path destination, boolean bundle, Path tempdir) throws IOException {

        // delete eventual previous file
        if (Files.exists(destination)) {
            Files.delete(destination);
        }

        if (bundle == true && tempdir == null) {
            throw new NullPointerException("If bundle is set to true, temporary directory must not be null");
        }

        // create a new temporary database
        Path tempProject;
        if (bundle) {
            tempProject = tempdir.resolve(PROJECT_TEMP_NAME);
        } else {
            tempProject = destination;
        }
        JDBCDataStore datastore = SQLUtils.getDatastoreFromH2(tempProject);

        try {
            writeMetadatas(project, tempProject);
        } catch (DAOException e) {
            throw new IOException(e);
        }

        // write layer contents
        for (AbstractLayer layer : project.getLayers()) {

            if (LayerType.FEATURES.equals(layer.getType())) {

                SimpleFeatureSource featureSource = ((FeatureLayer) layer).getFeatureSource();
                datastore.createSchema(featureSource.getSchema());

                FeatureStore featureStore = (FeatureStore) datastore.getFeatureSource(featureSource.getSchema().getTypeName());
                featureStore.addFeatures(featureSource.getFeatures());

            } else {
                logger.warning("Unknown type: " + layer.getType());
            }
        }

        datastore.dispose();

        if (bundle) {

            // compress temporary database
            Iterator<Path> dit = Files.newDirectoryStream(tempdir).iterator();
            ArrayList<Path> toCompress = new ArrayList<>();
            while (dit.hasNext()) {
                Path p = dit.next();
                if (p.toString().toLowerCase().endsWith(".db")) {
                    toCompress.add(p);
                }
            }

            ZipUtils.compress(toCompress, destination);

            // delete temporary files
            FileUtils.deleteDirectory(tempdir.toFile());

        }

        return true;

    }

    /**
     * Write metadatas and layer index to specified destination
     *
     * @param project
     * @param destination
     * @throws DAOException
     */
    public static void writeMetadatas(Project project, Path destination) throws DAOException {

        // write metadata
        ProjectMetadataDAO mtdao = new ProjectMetadataDAO(destination);
        mtdao.writeMetadata(project.getMetadataContainer());

        // write layer indexes
        LayerIndexDAO lidao = new LayerIndexDAO(destination);
        lidao.writeAllEntries(project.getLayerIndexEntries());

        // write styles
        StyleDAO sdao = new StyleDAO(destination);
        sdao.createTableIfNotExist();
        sdao.writeAll(project.getStyleLibrary().getStyleCollection());

    }

}
