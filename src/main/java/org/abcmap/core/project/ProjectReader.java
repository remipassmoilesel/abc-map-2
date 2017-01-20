package org.abcmap.core.project;

import org.abcmap.core.dao.LayoutDAO;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.dao.LayerIndexDAO;
import org.abcmap.core.dao.ProjectMetadataDAO;
import org.abcmap.core.dao.StyleDAO;
import org.abcmap.core.project.layers.*;
import org.abcmap.core.utils.SQLUtils;
import org.abcmap.core.utils.ZipUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        Path zipDump = tempfolder.resolve(ProjectWriter.PROJECT_ZIP_DUMP_NAME);
        Files.copy(projectFile, zipDump);

        // uncompress file
        ZipUtils.uncompress(zipDump, tempfolder);
        Files.delete(zipDump);

        Path dump = tempfolder.resolve(ProjectWriter.PROJECT_DUMP_NAME);

        // create a database and load dump
        Path newDatabase = tempfolder.resolve(ProjectWriter.PROJECT_TEMP_NAME);
        try {
            Connection conn = SQLUtils.getH2Connection(newDatabase);
            PreparedStatement loadStat = conn.prepareStatement("RUNSCRIPT FROM ?;");
            loadStat.setString(1, dump.toAbsolutePath().toString());
            loadStat.execute();

            // delete dump
            Files.delete(dump);

        } catch (SQLException e) {
            throw new IOException("Error while reading project", e);
        }

        Project newProject = new Project(newDatabase);
        newProject.setFinalPath(projectFile);

        // read metadata
        readMetadatas(newDatabase, newProject);

        // recreate layers
        LayerIndexDAO lidao = new LayerIndexDAO(newDatabase);
        ArrayList<LayerIndexEntry> indexes = lidao.readAllEntries();
        lidao.close();

        for (LayerIndexEntry entry : indexes) {

            // layer contain geometries
            if (AbmLayerType.FEATURES.equals(entry.getType())) {
                AbmFeatureLayer layer = new AbmFeatureLayer(entry, newProject);
                newProject.addLayer(layer);
            }

            // Layer contain tiles
            else if (AbmLayerType.TILES.equals(entry.getType())) {
                AbmTileLayer layer = new AbmTileLayer(entry, newProject);
                newProject.addLayer(layer);
            }

            // Layer contain tiles
            else if (AbmLayerType.WMS.equals(entry.getType())) {
                AbmWMSLayer layer = new AbmWMSLayer(entry, null, null, newProject);
                newProject.addLayer(layer);
            }

            // Layer contain tiles
            else if (AbmLayerType.SHAPE_FILE.equals(entry.getType())) {
                AbmShapeFileLayer layer = new AbmShapeFileLayer(entry, null, newProject);
                newProject.addLayer(layer);
            }

            // unrecognized layer
            else {
                logger.error("Unknown layer type: " + entry.getType());
                //TODO throw an exception ?
            }

        }

        // check if there is at least one layer
        if (newProject.getLayersList().size() < 1) {
            throw new IOException("Invalid project, no layers found");
        }

        // set the first layer active
        newProject.setFirstLayerActive();

        return newProject;
    }


    /**
     * Write metadata and layer index to specified destination
     *
     * @param project
     * @param source
     * @throws IOException
     */
    public void readMetadatas(Path source, Project project) throws IOException {

        // write metadata
        ProjectMetadataDAO mtdao = new ProjectMetadataDAO(source);
        project.setMetadataContainer(mtdao.readMetadata());
        mtdao.close();

        // get styles
        StyleDAO stdao = new StyleDAO(source);
        project.getStyleLibrary().setStyleCollection(stdao.readStyles());
        stdao.close();

        // read layouts
        LayoutDAO ldao = new LayoutDAO(source);
        project.setLayouts(ldao.readAll());
        ldao.close();


    }

}
