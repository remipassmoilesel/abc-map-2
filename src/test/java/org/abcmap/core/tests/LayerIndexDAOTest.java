package org.abcmap.core.tests;

import org.abcmap.TestConstants;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.LayerIndexEntry;
import org.abcmap.core.project.LayerType;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.LayerIndexDAO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class LayerIndexDAOTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        MainManager.init();
    }

    @Test
    public void tests() throws IOException, DAOException {

        // create layer index entries
        ArrayList<LayerIndexEntry> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new LayerIndexEntry(null, "Layer " + i, true, i, LayerType.FEATURES));
        }

        assertTrue("Creation test", list.equals(list));

        Path db = TestConstants.PLAYGROUND_DIRECTORY.resolve("layer_index.db");

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

        // scheme creation test
        LayerIndexDAO dao = new LayerIndexDAO(connection);

        // writing test
        dao.writeLayerIndex(list);

        // reading test
        ArrayList<LayerIndexEntry> readList = dao.readLayerIndex();
        assertTrue("Reading test", readList.equals(list));

    }

}
