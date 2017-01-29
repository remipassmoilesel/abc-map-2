package org.abcmap.tests.core.tile;

import com.labun.surf.Params;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.Main;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmTileLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.tileanalyse.TileComposer;
import org.abcmap.core.tileanalyse.TileSource;
import org.abcmap.core.tileanalyse.TileStorageSource;
import org.abcmap.core.tiles.TileContainer;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.data.WorldFileReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class TileComposerTest {

    private static final boolean SHOW_IN_WINDOW = true;

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void test() throws Exception {

        Project project = Main.getProjectManager().getProject();
        Path databasePath = project.getDatabasePath();

        project.addNewWMSLayer(GeoUtils.MUNDIALIS_WMS_URL, null);

        AbmTileLayer layer = (AbmTileLayer) project.addNewTileLayer("tileLayer1", true, project.getHigherZindex());

        Path imgPath = Paths.get("data/jdbc-mosaic-sample/map.tif");
        BufferedImage img = ImageIO.read(imgPath.toFile());
        assertTrue("Image reading: " + imgPath, img != null);

        System.out.println();
        System.out.println("img.getWidth() + ' ' + img.getHeight()");
        System.out.println(img.getWidth() + " " + img.getHeight());

        Path worldFilePath = Paths.get("data/jdbc-mosaic-sample/map.tfw");
        WorldFileReader worldFile = new WorldFileReader(worldFilePath.toFile());

        System.out.println("worldFile");
        System.out.println(worldFile.getXULC());
        System.out.println(worldFile.getYULC());
        System.out.println(worldFile.getXPixelSize());
        System.out.println(worldFile.getYPixelSize());

        double minx = worldFile.getXULC();
        double maxx = minx + 1060 * worldFile.getXPixelSize();
        double maxy = worldFile.getYULC();
        double miny = maxy - 588 * worldFile.getYPixelSize();


        TileContainer toStitch = new TileContainer(img, new ReferencedEnvelope(minx, maxx, miny, maxy, GeoUtils.WGS_84));

        System.out.println("toStitch.getArea()");
        System.out.println(toStitch.getArea());

        // add tile
        layer.addTile(toStitch);

        // try to update parameters
        //project.getTileStorage().setCoverageParameters(layer.getId(), minx, maxx, miny, maxy, 1, 1);

        if (SHOW_IN_WINDOW) {
            layer.refreshCoverage();
            project.showForDebugAndWait(true);
            Thread.sleep(30000);
        }


    }

    @Ignore
    @Test
    public void test2() throws Exception {

        // Try to use GENERIC_2D ?

        String root = "/tiles/osm_";

        Project project = Main.getProjectManager().getProject();
        Path databasePath = project.getDatabasePath();

        project.addNewWMSLayer(GeoUtils.MUNDIALIS_WMS_URL, null);

        // create a tile layer
        AbmTileLayer layer = (AbmTileLayer) project.addNewTileLayer("Tile layer 1", true, 2);

        // get surf configuration
        Params surfConfig = Main.getConfigurationManager().getSurfConfiguration();

        // create a tile composer, to get best positions for tiles
        TileComposer composer = new TileComposer(databasePath, surfConfig, 40, 5);
        TileSource source = new TileStorageSource(project.getTileStorage(), layer.getCoverageName());

        // the first tile have to be placed manually


        /*

        // ideal envelope, errors are raised if width < 1
        ReferencedEnvelope firstArea = new ReferencedEnvelope(
                3.749045,
                3.995306,
                43.600345,
                43.700178,
                GeoUtils.WGS_84
        );

        // rounded envelope, ugly as possible
        ReferencedEnvelope firstArea = new ReferencedEnvelope(
                2,
                5,
                40,
                50,
                GeoUtils.WGS_84
        );

        // envelope with image dimensions in pixel
        ReferencedEnvelope firstArea = new ReferencedEnvelope(
                0,
                796,
                0,
                898,
                null
        );
        */

        // envelope with lower dimensions, respecting width/height coeff
        ReferencedEnvelope firstArea = new ReferencedEnvelope(
                0,
                30,
                0,
                33.844221,
                null
        );


        System.out.println("firstArea");
        System.out.println(firstArea.getWidth());
        System.out.println(firstArea.getHeight());

        ArrayList<ReferencedEnvelope> expected = new ArrayList();
        expected.add(firstArea);
        /*expected.add(new Coordinate(-520.4633750915527, -18.264129638671875));
        expected.add(new Coordinate(-1078.8829231262207, 257.2473449707031));
        expected.add(new Coordinate(-1078.8697395324707, 32.2457275390625));*/

        ArrayList<ReferencedEnvelope> computed = new ArrayList();
        computed.add(firstArea);

        // iterate pictures
        for (int i = 0; i < 4; i++) {

            // read image
            String imgPath = root + (i + 1) + ".png";

            BufferedImage img = ImageIO.read(TileComposerTest.class.getResourceAsStream(imgPath));
            assertTrue("Image reading: " + imgPath, img != null);

            System.out.println();
            System.out.println("img.getWidth() + ' ' + img.getHeight()");
            System.out.println(img.getWidth() + " " + img.getHeight());

            TileContainer toStitch = new TileContainer(null, img, new ReferencedEnvelope());

            // first tile, add it manually
            if (i == 0) {
                toStitch.setArea(firstArea);
                layer.addTile(toStitch);
                continue;
            }

            // analyze tile to get best position
            ReferencedEnvelope newArea = composer.process(toStitch, source);
            assertTrue("Coordinate position test", newArea != null);

            computed.add(newArea);

            // set position
            toStitch.setArea(newArea);

            System.out.println("toStitch.getArea()");
            System.out.println(toStitch.getArea());

            // add tile
            layer.addTile(toStitch);

        }

        System.out.println(layer.getInternalLayer().getFeatureSource().getSchema().getCoordinateReferenceSystem());

        /*
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
        */

        if (SHOW_IN_WINDOW) {
            layer.refreshCoverage();
            project.showForDebug(true);
            Thread.sleep(150000);
        }


    }

}
