package org.abcmap;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.PrimitiveIterator;
import java.util.Random;

/**
 * Misc constants used in configuration cases
 */
public class TestUtils {

    public static final Path PLAYGROUND_DIRECTORY = Paths.get("tests-playground");
    public static final Path RESOURCES_DIRECTORY = Paths.get("src/test/resources");
    private static final Color[] randomColors = new Color[]{
            Color.red,
            Color.blue,
            Color.yellow,
            Color.black,
            Color.green,
    };
    private static final PrimitiveIterator.OfInt randomColorIndex = new Random().ints(0, randomColors.length).iterator();
    private static final PrimitiveIterator.OfInt randPointGeneric2d = new Random().ints(500, 2000).iterator();

    /**
     * Init the main manager if necessary
     *
     * @throws IOException
     */
    public static void mainManagerInit() throws IOException {
        if (MainManager.isInitialized() == false) {
            MainManager.init();
        }
    }

    /**
     * Initialize manager, close eventual existing project and create a new project
     *
     * @throws IOException
     */
    public static void createNewProject() throws IOException {

        mainManagerInit();

        ProjectManager pman = MainManager.getProjectManager();

        // close previous project if necessary
        if (pman.isInitialized()) {
            pman.closeProject();
        }

        // wait a litle between create too much projects
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // create new project
        pman.createNewProject();

    }

    public static Coordinate getRandomPoint() {
        return new Coordinate(randPointGeneric2d.next(), randPointGeneric2d.next());
    }

    public static Color getRandomColor() {
        return randomColors[randomColorIndex.next()];
    }

}
