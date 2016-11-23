package org.abcmap.tileanalyzer;

import com.labun.surf.Params;
import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.TestUtils;
import org.abcmap.core.project.layer.TileLayer;
import org.abcmap.core.project.tiles.TileContainer;
import org.abcmap.core.tileanalyser.*;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.map.GridCoverageLayer;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

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
    public void tests() throws IOException, TileAnalyseException, InterruptedException {

        String root = "/pictures/belledonne_";

        Project project = MainManager.getProjectManager().getProject();
        Path databasePath = project.getDatabasePath();

        TileLayer layer = (TileLayer) project.addNewTileLayer("Tile layer 1", true, 2);

        Params surfConfig = MainManager.getConfigurationManager().getSurfConfiguration();

        TileComposer composer = new TileComposer(databasePath, surfConfig, 5, 3);
        TileSource source = new TileStorageSource(project.getTileStorage(), layer.getCoverageName());

        // search points in pictures
        InterestPointFactory mpf = new InterestPointFactory(surfConfig);
        for (int i = 0; i < 4; i++) {

            // read image
            String imgPath = root + (i + 1) + ".jpg";
            BufferedImage img = ImageIO.read(TileComposerTest.class.getResourceAsStream(imgPath));
            assertTrue("Image reading: " + imgPath, img != null);

            TileContainer toStitch = new TileContainer(null, img, new Coordinate(0, 0));

            // first tile, add it manually
            if (i == 0) {
                layer.addTile(toStitch);
                continue;
            }

            // get best position for tile
            Coordinate position = composer.process(toStitch, source);

            assertTrue("Coordinate position test", position != null);

            toStitch.setPosition(position);

            // add tile
            layer.addTile(toStitch);

            System.out.println(toStitch);
        }

        layer.refreshCoverage();

        if (SHOW_IN_WINDOW) {
            project.showForDebug();
            //ImageIO.write(layer.getCoverage().getRenderedImage(), "png", new File("test.png"));
            Thread.sleep(50000);
        }

    }

}
