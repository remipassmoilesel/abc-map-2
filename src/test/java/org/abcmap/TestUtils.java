package org.abcmap;

import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.abcmap.core.managers.MainManager.getProjectManager;

/**
 * Misc constants used in configuration cases
 */
public class TestUtils {

    public static final Path PLAYGROUND_DIRECTORY = Paths.get("tests-playground");

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
            pman.closeProjet();
        }

        // create new project
        pman.createNewProject();

    }

}
