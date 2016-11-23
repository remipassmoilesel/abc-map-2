package org.abcmap.tileanalyzer;

import com.labun.surf.InterestPoint;
import com.labun.surf.Params;
import com.sun.media.jai.tilecodec.TileCodecUtils;
import org.abcmap.TestUtils;
import org.abcmap.core.tileanalyser.InterestPointContainer;
import org.abcmap.core.tileanalyser.InterestPointFactory;
import org.abcmap.core.tileanalyser.InterestPointStorage;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
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
public class InterestPointManagementTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        String root = "/pictures/belledonne_";

        Project project = MainManager.getProjectManager().getProject();
        Path databasePath = project.getDatabasePath();
        InterestPointStorage dao = new InterestPointStorage(databasePath);

        Params surfConfig = MainManager.getConfigurationManager().getSurfConfiguration();

        // search points in pictures
        InterestPointFactory mpf = new InterestPointFactory(surfConfig);
        for (int i = 0; i < 4; i++) {

            // read image
            String imgPath = root + (i + 1) + ".jpg";
            BufferedImage img = ImageIO.read(InterestPointManagementTest.class.getResourceAsStream(imgPath));
            assertTrue("Image reading: " + imgPath, img != null);

            // analyse it
            ArrayList<InterestPoint> points = mpf.getPointsList(img);
            assertTrue("Point search test: " + imgPath, points.size() > 0);

            // create a container
            InterestPointContainer ipc = new InterestPointContainer();
            ipc.setPoints(points);
            ipc.setTileId("image_" + i);
            ipc.setSurfConfigId(-1); // invalid id, in order to recreate entries later

            dao.create(ipc);
        }

        // read entries
        long entryNumber = dao.getRowCount();
        assertTrue("Reading entries " + entryNumber, entryNumber == 4);

    }

}
