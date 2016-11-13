package org.abcmap.core.project;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.LayerIndexDAO;
import org.abcmap.core.project.layer.Layer;
import org.abcmap.core.project.layer.LayerType;
import org.abcmap.core.utils.Utils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by remipassmoilesel on 10/11/16.
 */

public class ProjectReaderWriterTest {

    private static final GeometryFactory geom = JTSFactoryFinder.getGeometryFactory();

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() {

        try {
            Path tempDirectory = TestUtils.PLAYGROUND_DIRECTORY.resolve("projectIoTest");

            // clean previous directories
            if (Files.isDirectory(tempDirectory)) {
                Utils.deleteRecursively(tempDirectory.toFile());
            }
            Files.createDirectories(tempDirectory);

            // temp directory for creating project
            Path newProjectTempDirectory = tempDirectory.resolve("newProjectTempDirectory");
            Files.createDirectories(newProjectTempDirectory);

            ProjectWriter writer = new ProjectWriter();
            ProjectReader reader = new ProjectReader();

            // test creation
            Project newProject = writer.createNew(newProjectTempDirectory);
            newProject.getMetadataContainer().updateValue(PMConstants.TITLE, "New title of the death");

            assertTrue("Creation test",
                    Files.isRegularFile(newProjectTempDirectory.resolve(ProjectWriter.TEMPORARY_NAME)));

            // layer index test
            newProject.addNewLayer("Second layer", true, 1, LayerType.FEATURES);
            newProject.executeWithDatabaseConnection((connection) -> {
                LayerIndexDAO dao = null;
                try {
                    dao = new LayerIndexDAO(connection);
                    assertTrue("Layer index test", dao.readLayerIndex().size() == 2);
                } catch (DAOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            Layer l = newProject.getLayers().get(0);
            for (int i = 0; i < 100; i++) {
                l.addShape(geom.createPoint(new Coordinate(i, i)));
            }

            // Writing test
            Path writedProject = tempDirectory.resolve("writedProject.abm");
            writer.write(newProject, writedProject);

            assertTrue("Writing test", Files.isRegularFile(writedProject));

            // Basic project equality test
            assertTrue("Basic project equality test", newProject.equals(newProject));

            // Basic read test. Project is read twice, and we check if projects are same
            Path openProjectTempDirectory1 = tempDirectory.resolve("openProjectTempDirectory1");
            Path openProjectTempDirectory2 = tempDirectory.resolve("openProjectTempDirectory2");
            Files.createDirectories(openProjectTempDirectory1);
            Files.createDirectories(openProjectTempDirectory2);

            Project p1 = reader.read(openProjectTempDirectory1, writedProject);
            Project p2 = reader.read(openProjectTempDirectory2, writedProject);

            assertTrue("Metadata reading test 1", newProject.getMetadataContainer().equals(p1.getMetadataContainer()));
            assertTrue("Metadata reading test 2", p1.getMetadataContainer().equals(p2.getMetadataContainer()));

            assertTrue("Layers reading test 1", newProject.getLayers().equals(p1.getLayers()));
            assertTrue("Layers reading test 2", p1.getLayers().equals(p2.getLayers()));

            assertTrue("Project reading test 1", newProject.equals(p1));
            assertTrue("Project reading test 2", p1.equals(p2));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
