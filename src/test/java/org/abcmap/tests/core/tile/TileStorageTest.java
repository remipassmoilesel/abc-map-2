package org.abcmap.tests.core.tile;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.TestUtils;
import org.abcmap.core.tiles.TileContainer;
import org.abcmap.core.tiles.TileCoverageEntry;
import org.abcmap.core.tiles.TileStorage;
import org.abcmap.core.tiles.TileStorageQueries;
import org.abcmap.core.utils.SQLUtils;
import org.abcmap.core.utils.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
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
        TileCoverageEntry coverageEntry = storage.createCoverageStorage(coverageName);

        ArrayList<String> list = SQLUtils.getH2TableList(storage.getDatabaseConnection());

        assertTrue("Storage creation test", list.contains(TileStorageQueries.MASTER_TABLE_NAME));
        assertTrue("Coverage creation test 1", list.contains(TileStorageQueries.generateDataTableName(coverageName)));
        assertTrue("Coverage creation test 2", list.contains(TileStorageQueries.generateSpatialTableName(coverageName)));

        // add tiles to coverage
        Path tilesRoot = TestUtils.RESOURCES_DIRECTORY.resolve("tiles");
        Iterator<Path> dit = Files.newDirectoryStream(tilesRoot).iterator();
        int tileNumber = 0;
        int x = 0;
        int y = 0;
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<BufferedImage> imgs = new ArrayList<>();
        while (dit.hasNext()) {
            Path p = dit.next();

            // ignore non png
            if (p.toString().endsWith("png") == false) {
                continue;
            }

            // tiles ared added twice
            ids.add(storage.addTile(coverageName, p, new Coordinate(x, y)));

            BufferedImage img = ImageIO.read(p.toFile());
            imgs.add(img);

            x += 500;
            tileNumber++;
        }

        assertTrue("Insert tiles test 1", tileNumber > 0);
        assertTrue("Insert tiles test 2", tileNumber == ids.size());

        // test tile data
        try (Connection conn = storage.getDatabaseConnection()) {

            PreparedStatement selectStat = conn.prepareStatement("SELECT * " +
                    " FROM " + TileStorageQueries.generateDataTableName(coverageName) +
                    " ORDER BY " + TileStorageQueries.TILE_ID_FIELD_NAME + " ASC");

            ResultSet rslt = selectStat.executeQuery();

            int i = 0;
            while (rslt.next()) {

                Raster r1 = imgs.get(i).getData();
                Raster r2 = Utils.bytesToImage(rslt.getBytes(2)).getData();

                // compare random pixels
                for (int j = 0; j < 20; j += 8) {
                    assertTrue("Image comparison test " + i, r1.getPixel(j, j, (int[]) null)[0] == r2.getPixel(j, j, (int[]) null)[0]);
                    assertTrue("Image comparison test " + i, r1.getPixel(j, j, (int[]) null)[1] == r2.getPixel(j, j, (int[]) null)[1]);
                    assertTrue("Image comparison test " + i, r1.getPixel(j, j, (int[]) null)[2] == r2.getPixel(j, j, (int[]) null)[2]);
                }

                i++;
            }

        }


        try (Connection conn = storage.getDatabaseConnection()) {

            int i = 0;
            for (String id : ids) {

                // check if tile was inserted in spatial table
                PreparedStatement req = conn.prepareStatement("SELECT count(*) FROM " + coverageEntry.getSpatialTableName() + " " +
                        "WHERE " + TileStorageQueries.TILE_ID_FIELD_NAME + " = ?;");
                req.setString(1, id);

                ResultSet rs = req.executeQuery();
                rs.next();

                assertTrue("Insert tiles test 1:" + i, rs.getInt(1) == 1);

                // check if tile was inserted in data table
                req = conn.prepareStatement("SELECT count(*) FROM " + coverageEntry.getDataTableName()
                        + " WHERE " + TileStorageQueries.TILE_ID_FIELD_NAME + " = ?;");
                req.setString(1, id);

                rs = req.executeQuery();
                rs.next();

                assertTrue("Insert tiles test 2:" + i, rs.getInt(1) == 1);

                i++;
            }

            assertTrue("Insert tiles test 3", tileNumber == i);
        }

        // get last tiles test
        for (int i = ids.size() - 1, j = 0; i > 0; i--, j++) {

            ArrayList<TileContainer> ts = storage.getLastTiles(coverageName, j, 1);
            assertTrue("Last tiles retrieving test " + j, ts.size() == 1);

            String idA = ts.get(0).getTileId();
            String idB = ids.get(i);
            assertTrue("Last tiles retrieving test " + j + " : " + idA + " / " + idB, idA.equals(idB));

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
