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
import org.abcmap.core.utils.ZipUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;

/**
 * Contain methods to write a project
 */
public class ProjectWriter {

    private static final CustomLogger logger = LogManager.getLogger(ProjectWriter.class);

    /**
     * Temporary name of the project database when open
     */
    public static final String PROJECT_TEMP_NAME = "project." + ConfigurationConstants.PROJECT_EXTENSION + ".tmp";
    public static final String PROJECT_ZIP_DUMP_NAME = "dump.zip";
    public static final String PROJECT_DUMP_NAME = "dump.sql";

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
        try {
            pwriter.writeMetadatas(project, project.getDatabasePath());
        } catch (DAOException e) {
            throw new IOException("Error while creating a new project", e);
        }

        // add the first layer
        project.addNewFeatureLayer("First layer", true, 0);

        // set the first layer active
        project.setActiveLayer(0);

        return project;
    }


    /**
     * Write a project at specified destination
     *
     * @param project
     * @param destination
     * @throws IOException
     */
    public boolean export(Project project, Path destination) throws IOException {

        // write metadata into original project before dump
        try {
            writeMetadatas(project, project.getDatabasePath());
        } catch (DAOException e) {
            throw new IOException("Error while writing project", e);
        }

        // dump database
        Path dump = project.getTempDirectory().toAbsolutePath().resolve(PROJECT_DUMP_NAME);
        Files.deleteIfExists(dump);

        Boolean executed = (boolean) project.executeWithDatabaseConnection((conn) -> {

            PreparedStatement stat = conn.prepareStatement("SCRIPT DROP TO ?;");
            stat.setString(1, dump.toString());
            stat.execute();

            return true;

        });

        if (executed != true) {
            throw new IOException("Error while writing project");
        }

        // compress and copy to destination
        Path zipDump = project.getTempDirectory().resolve(PROJECT_ZIP_DUMP_NAME);
        Files.deleteIfExists(zipDump);

        ZipUtils.compress(project.getTempDirectory(), dump, zipDump);

        // copy to destination
        Files.copy(zipDump, destination);

        return true;

    }

    /**
     * Write metadatas and layer index to specified destination
     *
     * @param project
     * @param destination
     * @throws DAOException
     */
    public void writeMetadatas(Project project, Path destination) throws DAOException {

        // write metadata
        ProjectMetadataDAO mtdao = new ProjectMetadataDAO(destination);
        mtdao.writeMetadata(project.getMetadataContainer());
        mtdao.close();

        // write layer indexes
        LayerIndexDAO lidao = new LayerIndexDAO(destination);
        lidao.writeAllEntries(project.getLayerIndexEntries());
        lidao.close();

        // write styles
        StyleDAO sdao = new StyleDAO(destination);
        sdao.createTableIfNotExist();
        sdao.writeAll(project.getStyleLibrary().getStyleCollection());
        sdao.close();

    }

}
