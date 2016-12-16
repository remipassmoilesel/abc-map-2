package org.abcmap.tests.core.partials;


import junit.framework.TestCase;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.rendering.partials.RenderedPartial;
import org.abcmap.core.rendering.partials.RenderedPartialStore;
import org.abcmap.core.rendering.partials.SerializableRenderedPartial;
import org.abcmap.core.project.Project;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class PartialStoreTest {

    @BeforeClass
    public static void beforeTests() throws IOException, InterruptedException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException, SQLException {

        String root = "/tiles/osm_";

        // get partial store
        Project project = MainManager.getProjectManager().getProject();
        RenderedPartialStore store = project.getRenderedPartialsStore();

        ArrayList<RenderedPartial> partials = new ArrayList<>();

        // add some partials
        int totalToInsert = 4;
        for (int i = 0; i < totalToInsert; i++) {

            String imgPath = root + (i + 1) + ".png";

            BufferedImage img = ImageIO.read(PartialStoreTest.class.getResourceAsStream(imgPath));
            TestCase.assertTrue("Image reading: " + imgPath, img != null);

            ReferencedEnvelope ev = new ReferencedEnvelope(i, i + 1, i + 2, i + 3, DefaultGeographicCRS.WGS84);
            RenderedPartial part = new RenderedPartial(img, ev, img.getWidth(), img.getHeight(), "layer1");

            partials.add(part);

            store.addPartial(part);
        }

        // check if partials are in loaded list
        int loaded = store.getLoadedPartials().size();
        assertTrue("Loaded partials test: " + totalToInsert + " / " + loaded, loaded == totalToInsert);

        // check if partials have been added in database
        final int[] rowCount = {0};
        project.executeWithDatabaseConnection((conn) -> {

            PreparedStatement stat = conn.prepareStatement("SELECT count(*) FROM " + SerializableRenderedPartial.TABLE_NAME);
            ResultSet rslt = stat.executeQuery();
            rslt.next();

            rowCount[0] = rslt.getInt(1);

            stat.close();
            rslt.close();
            return null;
        });

        assertTrue("Database partials test: " + totalToInsert + " / " + rowCount[0], rowCount[0] == totalToInsert);

    }


}
