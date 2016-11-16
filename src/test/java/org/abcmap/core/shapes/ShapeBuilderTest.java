package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import jj2000.j2k.codestream.CoordInfo;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.Layer;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeomUtils;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Test on shapes builder
 */
public class ShapeBuilderTest {

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

        // test point building
        PointBuilder pointB = new PointBuilder();

        for (int i = 100; i < 120; i++) {
            SimpleFeature pt = pointB.addPoint(new Coordinate(i, i));
            SimpleFeature pt2 = activeLayer.getFeatureById(FeatureUtils.getId(pt));

            // Here equals() does not work with SimpleFeature (but work with SimpleFeatureImpl)
            assertTrue(pt.getID().equals(pt.getID()));
            assertTrue(pt.getID().equals(pt2.getID()));

            Geometry geom1 = (Geometry) pt.getDefaultGeometry();
            Geometry geom2 = (Geometry) pt2.getDefaultGeometry();

            assertTrue(geom1.equalsExact(geom1));
            assertTrue(geom1.equalsExact(geom2));
            assertTrue(geom1.equalsExact(geom.createPoint(new Coordinate(i, i))));
        }

        // test line building
        LineBuilder lineB = new LineBuilder();
        for (int i = 150; i < 170; i++) {

            ArrayList<Coordinate> points = new ArrayList<>();
            points.add(new Coordinate(i, i));
            points.add(new Coordinate(i + i, i));
            points.add(new Coordinate(i + i, i + i));
            points.add(new Coordinate(i, i + i));

            lineB.newLine(points.get(0));
            lineB.addPoint(points.get(1));
            lineB.addPoint(points.get(2));

            SimpleFeature line1 = lineB.terminateLine(points.get(3));
            SimpleFeature line2 = activeLayer.getFeatureById(FeatureUtils.getId(line1));

            assertTrue(line1.getID().equals(line1.getID()));
            assertTrue(line1.getID().equals(line2.getID()));

            Geometry geom1 = (Geometry) line1.getDefaultGeometry();
            Geometry geom2 = (Geometry) line2.getDefaultGeometry();

            assertTrue(geom1.equalsExact(geom1));
            assertTrue(geom1.equalsExact(geom2));
            assertTrue(geom1.equalsExact(geom.createLineString(points.toArray(new Coordinate[points.size()]))));

        }

        // test polygon building
        PolygonBuilder polyB = new PolygonBuilder();
        for (int i = 170; i < 200; i++) {

            ArrayList<Coordinate> points = new ArrayList<>();
            points.add(new Coordinate(i, i));
            points.add(new Coordinate(i + i, i));
            points.add(new Coordinate(i + i, i + i));
            points.add(new Coordinate(i, i + i));
            points.add(new Coordinate(i, i));

            polyB.newLine(points.get(0));
            polyB.addPoint(points.get(1));
            polyB.addPoint(points.get(2));
            polyB.addPoint(points.get(3));

            SimpleFeature poly1 = polyB.terminateLine(points.get(4));
            SimpleFeature poly2 = activeLayer.getFeatureById(FeatureUtils.getId(poly1));

            assertTrue(poly1.getID().equals(poly1.getID()));
            assertTrue(poly1.getID().equals(poly2.getID()));

            Geometry geom1 = (Geometry) poly1.getDefaultGeometry();
            Geometry geom2 = (Geometry) poly2.getDefaultGeometry();

            assertTrue(geom1.equalsExact(geom1));
            assertTrue(geom1.equalsExact(geom2));
            assertTrue(geom1.equalsExact(geom.createPolygon(points.toArray(new Coordinate[points.size()]))));

        }

    }

}