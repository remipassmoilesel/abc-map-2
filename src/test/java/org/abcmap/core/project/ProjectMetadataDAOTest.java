package org.abcmap.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.ProjectMetadataDAO;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class ProjectMetadataDAOTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException, DAOException, SQLException {

        Path tempfolder = TestUtils.PLAYGROUND_DIRECTORY.resolve("metadatasPersistenceTest");
        Files.createDirectories(tempfolder);

        Path db = tempfolder.resolve("metadatas.db");

        // clean previous db if necessary
        if(Files.exists(db)){
            Files.delete(db);
        }

        ProjectMetadata originalMtd = new ProjectMetadata();
        originalMtd.updateValue(PMConstants.CREATED, "NEW VALUE");

        assertTrue("Equality test", originalMtd.equals(originalMtd));
        assertTrue("Constant name test", PMConstants.safeValueOf("SOMETHING_NEVER_FOUND_#######") == null);

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
