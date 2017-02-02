package org.abcmap;

import com.j256.ormlite.logger.LocalLog;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.*;
import org.abcmap.gui.GuiColors;
import org.abcmap.gui.tools.containers.ToolLibrary;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by remipassmoilesel on 13/12/16.
 */
public class Initialization {

    protected static final CustomLogger logger = LogManager.getLogger(Initialization.class);

    private static final String DEV_MODE_ARG = "--dev-mode";
    private static final String CREATE_FAKE_ARG = "--create-fake-project";

    private Initialization(){

    }

    public static void doInit(String[] args) throws Exception {

        // parse arguments
        boolean devMode = false;
        String createFake = "";
        Path projectToOpen = null;

        if (args != null && args.length > 0) {
            for (String str : args) {

                // debug mode
                if (DEV_MODE_ARG.equalsIgnoreCase(str)) {
                    devMode = true;
                    logger.warning("Dev mode enabled");
                }

                // create fake project
                else if (str.indexOf(CREATE_FAKE_ARG) != -1) {
                    createFake = str;
                    logger.warning("A fake project will be created: " + str);
                }

                // open project
                else {
                    projectToOpen = Paths.get(str);
                    logger.warning("Project will be open: " + str);
                }
            }
        }

        // set dev mode
        Main.setDebugMode(devMode);
        EventNotificationManager.setDebugMode(devMode);

        // configure tier libraries
        configureLibraries();

        // initialize managers
        try {
            Main.init();
        } catch (Exception e) {
            logger.error(e);
            LaunchError.showErrorAndDie();
        }

        // gui initialization
        GuiManager guim = Main.getGuiManager();
        guim.configureUiManager();
        GuiColors.init();

        SwingUtilities.invokeAndWait(() -> {
            guim.constructGui();
        });

        ProjectManager pman = Main.getProjectManager();
        RecentManager rman = Main.getRecentManager();

        // open specified project
        if (projectToOpen != null) {

            try {

                pman.openProject(projectToOpen);

                rman.addCurrentProject();
                rman.saveHistory();
                rman.fireHistoryChanged();

            } catch (IOException e) {
                logger.error(e);
            }

        }
        // create a fake project
        else if (createFake.isEmpty() == false) {
            try {
                pman.createFakeProject(createFake);
            } catch (Exception e) {
                logger.error(e);
                pman.createNewProject();
            }

        }
        // or create a new project at launch
        else {
            pman.createNewProject();
        }

        // set default tool
        Main.getDrawManager().setCurrentTool(ToolLibrary.ZOOM_TOOL);

        // run gui initialization operation when all others operations started
        SwingUtilities.invokeAndWait(() -> {
            guim.runIntializationOperations();
        });

    }

    public static void configureLibraries() {

        // decrease database log
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty(LocalLog.LOCAL_LOG_PROPERTIES_FILE, "ormlite-log.txt");

        // force CRS axis order to match traditional x y order
        // if not set, put a CRS in H2 data store (eg WGS84) and retrieve it will produce two differents CRS
        // see: src/test/java/org/abcmap/tests/core/project/CRSAxisOrderTest.java
        System.setProperty("org.geotools.referencing.forceXY", "true");

    }
}
