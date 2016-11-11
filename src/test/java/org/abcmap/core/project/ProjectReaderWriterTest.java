package org.abcmap.core.project;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestConstants;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.*;
import org.abcmap.core.utils.Utils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 10/11/16.
 */

public class ProjectReaderWriterTest {

    private static final GeometryFactory geom = JTSFactoryFinder.getGeometryFactory();

    @BeforeClass
    public static void beforeTests() throws IOException {
        MainManager.init();
    }

    @Test
    public void tests() {

        try {
            Path tempDirectory = TestConstants.PLAYGROUND_DIRECTORY.resolve("projectIoTest");

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
            assertTrue("Creation test",
                    Files.isRegularFile(newProjectTempDirectory.resolve(ProjectWriter.TEMPORARY_NAME)));

            Layer l = newProject.getLayers().get(0);

            for (int i = 0; i < 100; i++) {
                l.addGeometry(geom.createPoint(new Coordinate(i, i)));
            }

            // Writing test
            Path writedProject = tempDirectory.resolve("writingProject.abm");
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

            assertTrue("Layers reading test", p1.getLayers().equals(p2.getLayers()));
            assertTrue("Metadata reading test", p1.getMetadata().equals(p2.getMetadata()));
            assertTrue("Reading test", p1.equals(p2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
