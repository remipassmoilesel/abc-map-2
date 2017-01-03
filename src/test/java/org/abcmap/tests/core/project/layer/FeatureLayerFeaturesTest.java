package org.abcmap.tests.core.project.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.Main;
import org.abcmap.core.project.Project;
import org.abcmap.core.draw.builder.DefaultSimpleFeatureBuilder;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test for add, find, retrieve features and geometries
 */
public class FeatureLayerFeaturesTest {


    private static FilterFactory ff = FeatureUtils.getFilterFactory();
    private static GeometryFactory geom = GeoUtils.getGeometryFactory();

    @Before
    public void beforeTest() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        Project project = Main.getProjectManager().getProject();
        FeatureLayer activeLayer = (FeatureLayer) project.getActiveLayer();

        // add a feature and retrieve it
        SimpleFeature point1 = activeLayer.addShape(geom.createPoint(new Coordinate(25, 58)));
        SimpleFeature point2 = activeLayer.getFeatureById(point1.getID());

        assertTrue("Add and find in layer test 1", point1.getID().equals(point1.getID()));
        assertTrue("Add and find in layer test 2", point1.getID().equals(point2.getID()));

        // add a feature and update it
        Geometry geomPoint = geom.createPoint(new Coordinate(2556, 548));
        SimpleFeature point3 = activeLayer.addShape(geomPoint);
        point3.setAttribute(DefaultSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME, "arbitrary_value");
        activeLayer.updateFeature(point3);

        final int[] count = {0};
        activeLayer.executeVisit((SimpleFeature f) -> {

            if (count[0] == 0) {
                assertTrue("Update feature test 1", f.getAttribute(DefaultSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME).equals(""));
            } else if (count[0] == 1) {
                assertTrue("Update feature test 2", f.getAttribute(DefaultSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME).equals("arbitrary_value"));
            }

            count[0]++;

            return true;
        });

        assertTrue("Update feature test 3", count[0] == 2);

    }

}
