package org.abcmap.tests.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.dao.ProjectMetadataDAO;
import org.abcmap.core.project.PMNames;
import org.abcmap.core.project.ProjectMetadata;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static junit.framework.TestCase.assertTrue;

/**
 * Basic DAO tests
 */
public class ProjectMetadataDAOTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.softwareInit();
    }

    @Test
    public void tests() throws IOException, SQLException {

        // create temp files
        Path tempfolder = TestUtils.PLAYGROUND_DIRECTORY.resolve("metadatasPersistenceTest");
        FileUtils.deleteDirectory(tempfolder.toFile());
        Files.createDirectories(tempfolder);

        Path db = tempfolder.resolve("metadatas.db");

        ProjectMetadata originalMtd = new ProjectMetadata();
        originalMtd.updateValue(PMNames.CREATED, "NEW VALUE");

        assertTrue("Equality test", originalMtd.equals(originalMtd));
        assertTrue("Constant name test", PMNames.safeValueOf("SOMETHING_NEVER_FOUND_#######") == null);

        // creation test
        ProjectMetadataDAO dao = new ProjectMetadataDAO(db);

        // writing test
        dao.writeMetadata(originalMtd);

        // reading test
        ProjectMetadata readMtd = dao.readMetadata();

        // Critical values
        //System.out.println(readMtd.getValue(PMConstants.CREATED));
        //System.out.println(originalMtd.getValue(PMConstants.CREATED));

        assertTrue("Reading test", readMtd.equals(originalMtd));

    }

}
