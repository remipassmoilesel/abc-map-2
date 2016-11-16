package org.abcmap.core.project.layer;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.apache.batik.gvt.font.FontFamilyResolver.resolve;

public class LayerIndexDAOTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException, DAOException, SQLException {

        Path tempfolder = TestUtils.PLAYGROUND_DIRECTORY.resolve("layerIndexPersistenceTest");
        Files.createDirectories(tempfolder);

        Path db = tempfolder.resolve("layer_index.db");

        // clean previous db if necessary
        if(Files.exists(db)){
            Files.delete(db);
        }

        // scheme creation test
        LayerIndexDAO dao = new LayerIndexDAO(db);

        // create layer index entries
        ArrayList<LayerIndexEntry> written = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LayerIndexEntry lie = new LayerIndexEntry(null, "Layer " + i, true, i, LayerType.FEATURES);
            written.add(lie);
            dao.create(lie);
        }

        assertTrue("Basic equality test", written.equals(written));

        // reading test
        ArrayList<LayerIndexEntry> readList = new ArrayList<>();
        dao.visit((Object o)->{
            LayerIndexEntry lie = (LayerIndexEntry) o;
            readList.add(lie);
            return true;
        });

        assertTrue("Reading test", readList.equals(written));

        dao.deleteAll();

        assertTrue("Delete test", dao.readAllEntries().size() == 0);

    }

}
