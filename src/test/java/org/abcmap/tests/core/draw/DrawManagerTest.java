package org.abcmap.tests.core.draw;


import org.abcmap.TestUtils;
import org.abcmap.core.draw.AbmGeometryType;
import org.abcmap.core.draw.DrawManagerException;
import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.draw.builder.PointBuilder;
import org.abcmap.core.draw.builder.PolygonBuilder;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.utils.FeatureUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class DrawManagerTest {

    @BeforeClass
    public static void beforeTests() throws IOException, InterruptedException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException, DrawManagerException, InterruptedException {

        DrawManager dm = Main.getDrawManager();
        ProjectManager pm = Main.getProjectManager();
        AbmFeatureLayer activeLayer = (AbmFeatureLayer) pm.getProject().getActiveLayer();

        int pointNumber = 20;
        int lineNumber = 20;
        int polygonNumber = 20;

        // change style
        dm.setActiveForeground(Color.red);
        dm.setActiveBackground(Color.black);

        // draw random points
        PrimitiveIterator.OfInt randSummits = new Random().ints(4, 16).iterator();
        PointBuilder pbuilder = dm.getPointBuilder();
        for (int i = 0; i < pointNumber; i++) {
            pbuilder.addPoint(TestUtils.getRandomPoint());
        }

        // change style
        dm.setActiveForeground(Color.DARK_GRAY);
        dm.setActiveBackground(Color.green);

        // draw random lines
        LineBuilder lbuilder = dm.getLineBuilder();
        for (int i = 0; i < lineNumber; i++) {
            lbuilder.newLine(TestUtils.getRandomPoint());

            int s = randSummits.next();
            for (int j = 0; j < s; j++) {
                lbuilder.addPoint(TestUtils.getRandomPoint());
            }
            lbuilder.terminateLine(TestUtils.getRandomPoint());
        }

        // change style
        dm.setActiveForeground(Color.cyan);
        dm.setActiveBackground(Color.yellow);

        // draw random polygons
        PolygonBuilder plbuilder = dm.getPolygonBuilder();
        for (int i = 0; i < polygonNumber; i++) {
            plbuilder.newLine(TestUtils.getRandomPoint());

            int s = randSummits.next();
            for (int j = 0; j < s; j++) {
                plbuilder.addPoint(TestUtils.getRandomPoint());
            }
            plbuilder.terminateLine(TestUtils.getRandomPoint());
        }

        // check that all draw have a style id
        final int[] shapeCount = {0};
        activeLayer.executeVisit((SimpleFeature f) -> {

            // test style numericalId
            String sid = FeatureUtils.getStyleId(f);
            assertTrue("Style test", sid != null);

            shapeCount[0]++;
            return true;
        });

        // this style have to be already created, and must not change style count
        dm.setActiveForeground(Color.red);
        dm.setActiveBackground(Color.black);
        dm.getActiveStyle(AbmGeometryType.LINE);

        // test number of draw
        assertTrue("Shape number test", shapeCount[0] == pointNumber + lineNumber + polygonNumber);

        // test number of created styles
        assertTrue("Style count test", pm.getProject().getStyleLibrary().getStyleCollection().size() == 3);

    }


}
