package org.abcmap.tests.core.tile;

import com.labun.surf.Params;
import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.TileLayer;
import org.abcmap.core.tileanalyser.TileAnalyseException;
import org.abcmap.core.tileanalyser.TileComposer;
import org.abcmap.core.tileanalyser.TileSource;
import org.abcmap.core.tileanalyser.TileStorageSource;
import org.abcmap.core.tiles.TileContainer;
import org.abcmap.core.utils.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class TileComposerTest {

    private static final boolean SHOW_IN_WINDOW = false;

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException, TileAnalyseException, InterruptedException {

        String root = "/tiles/osm_";

        Project project = MainManager.getProjectManager().getProject();
        Path databasePath = project.getDatabasePath();

        // create a tile layer
        TileLayer layer = (TileLayer) project.addNewTileLayer("Tile layer 1", true, 2);

        // get surf configuration
        Params surfConfig = MainManager.getConfigurationManager().getSurfConfiguration();

        // create a tile composer, to get best positions for tiles
        TileComposer composer = new TileComposer(databasePath, surfConfig, 40, 5);
        TileSource source = new TileStorageSource(project.getTileStorage(), layer.getCoverageName());

        ArrayList<Coordinate> coords = new ArrayList<>();
        ArrayList<BufferedImage> imgs = new ArrayList<>();

        // the first tile have to be placed manually
        Coordinate firstPosition = new Coordinate(0, 0);

        ArrayList<Coordinate> expected = new ArrayList();
        expected.add(firstPosition);
        expected.add(new Coordinate(-520.4633750915527, -18.264129638671875));
        expected.add(new Coordinate(-1078.8829231262207, 257.2473449707031));
        expected.add(new Coordinate(-1078.8697395324707, 32.2457275390625));

        ArrayList<Coordinate> computed = new ArrayList();
        computed.add(firstPosition);

        // iterate pictures
        for (int i = 0; i < 4; i++) {

            // read image
            String imgPath = root + (i + 1) + ".png";

            BufferedImage img = ImageIO.read(TileComposerTest.class.getResourceAsStream(imgPath));
            assertTrue("Image reading: " + imgPath, img != null);
            imgs.add(img);

            TileContainer toStitch = new TileContainer(null, img, new Coordinate());

            // first tile, add it manually
            if (i == 0) {
                toStitch.setPosition(firstPosition);
                coords.add(firstPosition);
                layer.addTile(toStitch);
                continue;
            }

            // analyze tile to get best position
            Coordinate position = composer.process(toStitch, source);
            assertTrue("Coordinate position test", position != null);

            computed.add(position);

            // set position
            coords.add(position);
            toStitch.setPosition(position);

            // add tile
            layer.addTile(toStitch);

        }

        // test is rounded here to avoid bad precision errors
        for (int i = 0; i < expected.size(); i++) {

            Coordinate ca = expected.get(i);
            Coordinate cb = computed.get(i);

            double cax = Utils.round(ca.x, 3);
            double cay = Utils.round(ca.y, 3);
            double cbx = Utils.round(cb.x, 3);
            double cby = Utils.round(cb.y, 3);

            assertTrue("Tile computed positions: " + cax + " " + cbx, cax - cbx < 1);
            assertTrue("Tile computed positions: " + cay + " " + cby, cay - cby < 1);
        }

        if (SHOW_IN_WINDOW) {
            layer.refreshCoverage();
            project.showForDebug(true);
            Thread.sleep(50000);
        }


    }

}
