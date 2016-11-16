package org.abcmap.core.draw;


import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.dao.DAOException;
import org.abcmap.core.project.dao.StyleDAO;
import org.abcmap.core.shapes.LineBuilder;
import org.abcmap.core.shapes.PointBuilder;
import org.abcmap.core.shapes.PolygonBuilder;
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
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws DAOException {

        DrawManager dm = MainManager.getDrawManager();
        ProjectManager pm = MainManager.getProjectManager();

        int pointNumber = 20;
        int lineNumber = 20;
        int polygonNumber = 20;

        PrimitiveIterator.OfInt randSummits = new Random().ints(4, 16).iterator();

        PointBuilder pbuilder = dm.getPointBuilder();
        for (int i = 0; i < pointNumber; i++) {
            pbuilder.addPoint(TestUtils.getRandomPoint());
        }

        dm.setActiveForeground(Color.red);
        dm.setActiveBackground(Color.black);

        LineBuilder lbuilder = dm.getLineBuilder();
        for (int i = 0; i < lineNumber; i++) {
            lbuilder.newLine(TestUtils.getRandomPoint());

            int s = randSummits.next();
            for (int j = 0; j < s; j++) {
                lbuilder.addPoint(TestUtils.getRandomPoint());
            }
            lbuilder.terminateLine(TestUtils.getRandomPoint());
        }

        dm.setActiveForeground(Color.cyan);
        dm.setActiveBackground(Color.yellow);

        PolygonBuilder plbuilder = dm.getPolygonBuilder();
        for (int i = 0; i < polygonNumber; i++) {
            plbuilder.newLine(TestUtils.getRandomPoint());

            int s = randSummits.next();
            for (int j = 0; j < s; j++) {
                plbuilder.addPoint(TestUtils.getRandomPoint());
            }
            plbuilder.terminateLine(TestUtils.getRandomPoint());
        }


        final int[] shapeCount = {0};
        pm.getProject().getActiveLayer().executeVisit((SimpleFeature f) -> {

            // test style id
            String sid = FeatureUtils.getStyleId(f);
            assertTrue("Style test", sid != null);

            shapeCount[0]++;
            return true;
        });

        // this style have to be already created, and must not change style count
        dm.setActiveForeground(Color.red);
        dm.setActiveBackground(Color.black);
        dm.getActiveStyle();

        // test number of shapes
        assertTrue("Shape number test", shapeCount[0] == pointNumber + lineNumber + polygonNumber);

        // test number of created styles
        assertTrue("Style count test", pm.getProject().getStyleLibrary().getStyleCollection().size() == 3);

    }


}
