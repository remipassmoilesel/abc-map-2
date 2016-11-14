package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeomUtils;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test for add, find, retrieve features and geometries
 */
public class LayerFeaturesTest {


    private static FilterFactory ff = FeatureUtils.getFilterFactory();
    private static GeometryFactory geom = GeomUtils.getGeometryFactory();

    @Before
    public void beforeTest() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        Project project = MainManager.getProjectManager().getProject();
        Layer activeLayer = project.getActiveLayer();

        SimpleFeature point1 = activeLayer.addShape(geom.createPoint(new Coordinate(25, 58)));
        SimpleFeature point2 = activeLayer.getFeatureById(point1.getID());

        assertTrue("Add and find in layer test 1", point1.getID().equals(point1.getID()));
        assertTrue("Add and find in layer test 2", point1.getID().equals(point2.getID()));

        Geometry geomPoint = geom.createPoint(new Coordinate(2556, 548));
        SimpleFeature point3 = activeLayer.addShape(geomPoint);
        SimpleFeature point4 = activeLayer.addFeature(point3);
        SimpleFeature point5 = activeLayer.getFeatureById(point3.getID());

        assertTrue("Overwrite feature test 1", point3.getID().equals(point3.getID()));
        assertTrue("Overwrite feature test 2", point3.getID().equals(point4.getID()));
        assertTrue("Overwrite feature test 3", point3.getID().equals(point5.getID()));
        assertTrue("Overwrite feature test 4", point4.getID().equals(point5.getID()));

        Geometry geomPoint2 = (Geometry) point5.getDefaultGeometry();
        assertTrue("Overwrite geometry test", geomPoint.equalsExact(geomPoint));
        assertTrue("Overwrite geometry test 2", geomPoint.equalsExact(geomPoint2));

    }

}
