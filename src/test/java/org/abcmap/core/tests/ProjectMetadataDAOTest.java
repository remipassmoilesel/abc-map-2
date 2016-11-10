package org.abcmap.core.tests;

import org.abcmap.TestConstants;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.ProjectMetadata;
import org.abcmap.core.project.PMConstants;
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
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class ProjectMetadataDAOTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        MainManager.init();
    }

    @Test
    public void tests() throws IOException, DAOException {

        ProjectMetadata defaultMts = new ProjectMetadata();

        assertTrue("Equality test", defaultMts.equals(defaultMts));

        assertTrue("Constant name test", PMConstants.safeValueOf("SOMETHING_NEVER_FOUND_#######") == null);

        Path db = TestConstants.PLAYGROUND_DIRECTORY.resolve("metadatas.db");

        // clean previous db if necessary
        if(Files.exists(db)){
            Files.delete(db);
        }

        // open connection to db
        Map params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", db.toString());

        JDBCDataStore datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
        Connection connection = datastore.getConnection(Transaction.AUTO_COMMIT);

        // creation test
        ProjectMetadataDAO dao = new ProjectMetadataDAO(connection);

        // writing test
        dao.writeMetadata(defaultMts);

        // reading test
        ProjectMetadata mtsR = dao.readMetadata();
        assertTrue("Reading test", mtsR.equals(defaultMts));

    }

}
