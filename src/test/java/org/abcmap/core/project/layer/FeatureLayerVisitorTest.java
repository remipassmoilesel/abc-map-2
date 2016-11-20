package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.GeoUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Simple feature visitor
 */
public class FeatureLayerVisitorTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        Project project = MainManager.getProjectManager().getProject();
        FeatureLayer activeLayer = (FeatureLayer) project.getActiveLayer();

        GeometryFactory geom = GeoUtils.getGeometryFactory();

        for (int i = 0; i < 50; i++) {
            activeLayer.addShape(geom.createPoint(new Coordinate(i, i)));
        }

        final int[] i = {0};
        activeLayer.executeVisit((feature) -> {

            i[0]++;

            if (i[0] == 17) {
                return false;
            }

            return true;
        }, null);

        assertTrue("Visitor test", i[0] == 17);

    }

}
