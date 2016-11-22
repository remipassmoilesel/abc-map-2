package org.abcmap.imageanalyzer;

import org.abcmap.TestUtils;
import org.abcmap.core.imageanalyzer.MatchablePointContainer;
import org.abcmap.core.imageanalyzer.MatchablePointFactory;
import org.abcmap.core.imageanalyzer.MatchablePointStorage;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.dao.DAOException;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class MatchablePointsTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException, DAOException {

        String root = "/pictures/belledonne_";

        Project project = MainManager.getProjectManager().getProject();
        Path databasePath = project.getDatabasePath();
        MatchablePointStorage dao = new MatchablePointStorage(databasePath);

        // search points in pictures
        MatchablePointFactory mpf = new MatchablePointFactory();
        for (int i = 0; i < 4; i++) {

            // read image
            String imgPath = root + (i + 1) + ".jpg";
            BufferedImage img = ImageIO.read(MatchablePointsTest.class.getResourceAsStream(imgPath));
            assertTrue("Image reading: " + imgPath, img != null);

            // analyse it
            MatchablePointContainer pmc = mpf.getPointsContainer(img, "image_" + i);
            assertTrue("Point search test: " + imgPath, pmc.getPoints().size() > 0);

            // write it
            dao.create(pmc);
        }

        // read entries
        long entryNumber = dao.getRowCount();
        assertTrue("Reading entries " + entryNumber, entryNumber == 4);

    }


}
