package org.abcmap.core.partials;

import com.vividsolutions.jts.geom.Coordinate;
import junit.framework.TestCase;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.TileLayer;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.gui.components.map.CachedMapPane;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class PartialFactoryTest {

    private static final boolean SHOW_IN_WINDOW = false;

    @BeforeClass
    public static void beforeTests() throws IOException, InterruptedException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException, SQLException, InterruptedException {

        String root = "/tiles/osm_";

        // get partial store
        Project project = MainManager.getProjectManager().getProject();

        // create a tile layer
        TileLayer layer = (TileLayer) project.addNewTileLayer("Tile layer 1", true, 0);

        // TODO: get more realistics coordinates
        ArrayList<Coordinate> positions = new ArrayList();
        positions.add(new Coordinate(2000, 1000));
        positions.add(new Coordinate(1478.6803359985352, 982.1077270507812));
        positions.add(new Coordinate(919.2462692260742, 1257.8359985351562));
        positions.add(new Coordinate(919.2940902709961, 1032.83740234375));

        int totalToInsert = 4;
        for (int i = 0; i < totalToInsert; i++) {

            String imgPath = root + (i + 1) + ".png";

            BufferedImage img = ImageIO.read(PartialFactoryTest.class.getResourceAsStream(imgPath));
            TestCase.assertTrue("Image reading: " + imgPath, img != null);

            layer.addTile(img, positions.get(i));
        }

        // try to get partials
        Point2D.Double worldPosition = new Point2D.Double(1000, 2000);
        int partialSideWu = 700;
        Dimension dimensionsPx = new Dimension(1200, 1200);

        RenderedPartialFactory factory = new RenderedPartialFactory(project.getPartialStore(), project.buildMapContent(true));
        final int[] renderedTiles = {0};
        RenderedPartialQueryResult queryResult = factory.intersect(worldPosition, dimensionsPx, DefaultEngineeringCRS.GENERIC_2D, () -> {
            System.out.println("New partial added: " + renderedTiles[0]);
            renderedTiles[0]++;
        });

        // test partial creation
        int expected = 9;
        int partialNumber = queryResult.getPartials().size();
        assertTrue("Partial creation test: " + partialNumber + " / " + expected, partialNumber == expected);

        // test partial rendering
        if (SHOW_IN_WINDOW == false) {

            // wait until partials have been rendered
            int waitTime = 100;
            int maxTime = 3000;
            int currentTime = 0;
            while (currentTime < maxTime) {
                if (renderedTiles[0] == expected) {
                    break;
                }
                Thread.sleep(waitTime);
                currentTime += waitTime;
            }

            // IF TEST FAIL: try waiting longer, see above
            assertTrue("Partial rendering test: " + renderedTiles[0] + " / " + expected, renderedTiles[0] == expected);

        } else {

            layer.refreshCoverage();

            CachedMapPane pane = new CachedMapPane(project.getPartialStore(), project.buildMapContent(true));

            //pane.setWorldBounds(start);
            pane.setWorldPosition(worldPosition);
            pane.setPartialSideWu(partialSideWu);
            pane.setShowGrid(true);
            pane.setMouseManagementEnabled(true);

            JFrame frame = new JFrame();
            frame.setContentPane(pane);
            frame.setSize(dimensionsPx);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setVisible(true);

            pane.initializeMap();

            Thread.sleep(50000);
        }

    }

}
