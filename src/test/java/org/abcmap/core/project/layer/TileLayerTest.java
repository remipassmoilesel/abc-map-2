package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Coordinate;
import junit.framework.Assert;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.SQLUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.FactoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class TileLayerTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException, FactoryException {

        // create temp directories
        Path root = TestUtils.PLAYGROUND_DIRECTORY.resolve("tileLayerTest");
        Utils.deleteDirectories(root.toFile());

        ProjectManager pman = MainManager.getProjectManager();
        Project project = pman.getProject();
        TileLayer layer = (TileLayer) project.addNewTileLayer("Tile layer 1", true, 2);

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

            // add tiles twice
            ids.add(layer.addTile(p, new Coordinate(x, y)));
            ids.add(layer.addTile(p, new Coordinate(x, y)));

            x += 500;
            tileNumber++;
            tileNumber++;
        }

        String outlinesTableName = layer.getId();

        // count inserted outlines
        int finalTileNumber = tileNumber;
        project.executeWithDatabaseConnection((conn) -> {

            PreparedStatement selectStat = conn.prepareStatement("SELECT count(*) FROM " + outlinesTableName);
            ResultSet rslt = selectStat.executeQuery();
            int rowCount = rslt.getInt(1);

            assertTrue("Tile outline test 1", rowCount == finalTileNumber);

            return null;
        });

        // delete some tiles
        layer.removeTiles(ids.subList(0, 5));
        project.executeWithDatabaseConnection((conn) -> {

            PreparedStatement selectStat = conn.prepareStatement("SELECT count(*) FROM " + outlinesTableName);
            ResultSet rslt = selectStat.executeQuery();
            int rowCount = rslt.getInt(1);

            assertTrue("Tile outline test 2", rowCount == finalTileNumber - 5);

            return null;
        });

        assertTrue("Tile coverage test", layer.getInternalLayer() != null);
    }

}