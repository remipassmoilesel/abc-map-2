package org.abcmap.core.tests;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestConstants;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.ProjectReader;
import org.abcmap.core.project.ProjectWriter;
import org.abcmap.core.utils.Utils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void tests()  {

        try {
            Path tempDirectory = TestConstants.PLAYGROUND_DIRECTORY.resolve("readWriteTest");

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
            Project p = writer.createNew(newProjectTempDirectory);
            assertTrue("Creation test",
                    Files.isRegularFile(newProjectTempDirectory.resolve(ProjectWriter.TEMPORARY_NAME)));

            for (int i = 0; i < 100; i++) {

            }

            // test writing
            Path finalPath = tempDirectory.resolve("writingProject.abm");
            writer.write(p, finalPath);
            assertTrue("Writing test", Files.isRegularFile(finalPath));

            // basic project equality
            assertTrue("Basic project equality test", p.equals(p));

            Project p2 = reader.read(tempDirectory, finalPath);

            // read / write project equality
            // here test can fail because of creation dates
            assertTrue(p.equals(p2));

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
