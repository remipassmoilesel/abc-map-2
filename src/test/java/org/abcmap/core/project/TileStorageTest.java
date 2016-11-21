package org.abcmap.core.project;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.TestUtils;
import org.abcmap.core.project.tiles.TileCoverageEntry;
import org.abcmap.core.project.tiles.TileStorage;
import org.abcmap.core.utils.SQLUtils;
import org.abcmap.core.utils.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import static junit.framework.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 18/11/16.
 */
public class TileStorageTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException, SQLException {

        // create temp directories
        Path tempDirectory = TestUtils.PLAYGROUND_DIRECTORY.resolve("tileStorageTest");
        Utils.deleteDirectories(tempDirectory.toFile());

        Files.createDirectories(tempDirectory);

        // create a database
        Path databasePath = tempDirectory.resolve("tileStorage.h2");

        // create a tile storage
        TileStorage storage = new TileStorage(databasePath);
        storage.initialize();

        // create a new coverage, upper case mandatory
        String coverageName = "COVERAGE1";
        TileCoverageEntry coverageEntry = storage.createCoverage(coverageName);

        ArrayList<String> list = SQLUtils.getH2TableList(storage.getDatabaseConnection());

        assertTrue("Storage creation test", list.contains(TileStorage.MASTER_TABLE_NAME));
        assertTrue("Coverage creation test 1", list.contains(TileStorage.DATA_TABLE_PREFIX + coverageName));
        assertTrue("Coverage creation test 2", list.contains(TileStorage.SPATIAL_TABLE_PREFIX + coverageName));

        // add tiles to coverage
        Path tilesRoot = TestUtils.RESOURCES_DIRECTORY.resolve("tiles");
        Iterator<Path> dit = Files.newDirectoryStream(tilesRoot).iterator();
        int tileNumber = 0;
        int x = 0;
        int y = 0;
        ArrayList<String> ids = new ArrayList<>();
        while (dit.hasNext()) {
            Path p = dit.next();

            // ignore non png
            if (p.toString().endsWith("png") == false) {
                continue;
            }

            // tiles ared added twice
            ids.add(storage.addTile(coverageName, p, new Coordinate(x, y)));
            ids.add(storage.addTile(coverageName, p, new Coordinate(x, y)));

            x += 500;
            tileNumber++;
            tileNumber++;
        }

        assertTrue("Insert tiles test 1", tileNumber > 0);
        assertTrue("Insert tiles test 2", tileNumber == ids.size());

        try (Connection conn = storage.getDatabaseConnection()) {

            int i = 0;
            for (String id : ids) {

                // check if tile was inserted in spatial table
                PreparedStatement req = conn.prepareStatement("SELECT count(*) FROM " + coverageEntry.getSpatialTableName() + " " +
                        "WHERE " + TileStorage.TILE_ID_FIELD_NAME + " = ?;");
                req.setString(1, id);

                ResultSet rs = req.executeQuery();
                rs.next();

                assertTrue("Insert tiles test 1:" + i, rs.getInt(1) == 1);

                // check if tile was inserted in data table
                req = conn.prepareStatement("SELECT count(*) FROM " + coverageEntry.getDataTableName()
                        + " WHERE " + TileStorage.TILE_ID_FIELD_NAME + " = ?;");
                req.setString(1, id);

                rs = req.executeQuery();
                rs.next();

                assertTrue("Insert tiles test 2:" + i, rs.getInt(1) == 1);

                i++;
            }

            assertTrue("Insert tiles test 3", tileNumber == i);
        }

        // delete tiles
        boolean deleteOne = storage.deleteTile(coverageName, ids.remove(0));
        assertTrue("Deletion test 1", deleteOne);

        ArrayList<String> toRemove = new ArrayList();
        int nbrToRemove = 3;
        int i = 0;
        while (i < nbrToRemove) {
            toRemove.add(ids.remove(0));
            i++;
        }

        boolean deleteSeveral = storage.deleteTiles(coverageName, toRemove);
        assertTrue("Deletion test 2", deleteSeveral);

        // move tiles
        boolean moveOne = storage.moveTile(coverageName, ids.get(0), 2000d, 2000d);
        assertTrue("Move tile test 1", moveOne);

        ArrayList<Object[]> toMove = new ArrayList<>();
        toMove.add(new Object[]{ids.get(1), 2500d, 2500d});
        toMove.add(new Object[]{ids.get(2), 2400d, 2400d});
        toMove.add(new Object[]{ids.get(3), 2300d, 2300d});
        boolean moveSeveral = storage.moveTiles(coverageName, toMove);
        assertTrue("Move tile test 2", moveSeveral);


    }

}
