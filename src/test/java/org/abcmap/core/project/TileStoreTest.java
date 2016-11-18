package org.abcmap.core.project;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.TestUtils;
import org.abcmap.core.project.tiles.TileCoverageEntry;
import org.abcmap.core.project.tiles.TileStore;
import org.abcmap.core.utils.SQLiteUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.geopkg.GeoPackage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
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
public class TileStoreTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException, SQLException {

        // create temp directories
        Path root = TestUtils.PLAYGROUND_DIRECTORY.resolve("tileStorageTest");
        Utils.deleteDirectories(root.toFile());

        Files.createDirectories(root);

        // create a geopackage
        Path geopkPath = root.resolve("tileStorage.geopk");

        GeoPackage geopk = new GeoPackage(geopkPath.toFile());
        geopk.init();

        // create a tile storage
        TileStore storage = new TileStore(geopkPath);
        storage.initialize();

        // create a new coverage
        String coverageName = "coverage1";
        TileCoverageEntry coverageEntry = storage.addCoverage(coverageName);

        ArrayList<String> list = SQLiteUtils.getTableList(storage.getDatabaseConnection());

        assertTrue("Storage creation test", list.contains(TileStore.MASTER_TABLE_NAME));
        assertTrue("Coverage creation test 1", list.contains(TileStore.DATA_TABLE_PREFIX + coverageName));
        assertTrue("Coverage creation test 2", list.contains(TileStore.SPATIAL_TABLE_PREFIX + coverageName));

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

            ids.add(storage.addTile(coverageName, new Coordinate(x, y), p));

            x += 500;
            tileNumber++;
        }

        assertTrue("Insert tiles test 1", tileNumber > 0);
        assertTrue("Insert tiles test 2", tileNumber == ids.size());

        try (Connection conn = storage.getDatabaseConnection()) {

            int i = 0;
            for (String id : ids) {

                // check if tile was inserted in spatial table
                PreparedStatement req = conn.prepareStatement("SELECT count(*) FROM " + coverageEntry.getSpatialTableName() + " WHERE " + TileStore.TILE_ID_FIELD_NAME + " = ?;");
                req.setString(1, id);
                ResultSet rs = req.executeQuery();

                assertTrue("Insert tiles test 1:" + i, rs.getInt(1) == 1);

                // check if tile was inserted in data table
                req = conn.prepareStatement("SELECT count(*) FROM " + coverageEntry.getDataTableName() + " WHERE " + TileStore.TILE_ID_FIELD_NAME + " = ?;");
                req.setString(1, id);
                rs = req.executeQuery();

                assertTrue("Insert tiles test 2:" + i, rs.getInt(1) == 1);

                i++;
            }

            assertTrue("Insert tiles test 3", tileNumber == i);
        }

    }

}
