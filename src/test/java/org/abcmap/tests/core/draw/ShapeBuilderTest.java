package org.abcmap.tests.core.draw;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.draw.builder.PointBuilder;
import org.abcmap.core.draw.builder.PolygonBuilder;
import org.abcmap.core.managers.Main;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

/**
 * Test on draw builder
 */
public class ShapeBuilderTest {

    private static FilterFactory ff = FeatureUtils.getFilterFactory();
    private static GeometryFactory geom = GeoUtils.getGeometryFactory();

    @Before
    public void beforeTest() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        Project project = Main.getProjectManager().getProject();
        AbmFeatureLayer activeLayer = (AbmFeatureLayer) project.getActiveLayer();

        // test point building
        PointBuilder pointB = new PointBuilder(activeLayer, project.getStyle(Color.black, Color.white, 5));

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
        LineBuilder lineB = new LineBuilder(activeLayer, null);
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
        PolygonBuilder polyB = new PolygonBuilder(activeLayer, null);
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
