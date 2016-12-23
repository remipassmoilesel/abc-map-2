package org.abcmap;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.gui.GuiColors;
import org.abcmap.gui.tools.containers.ToolLibrary;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by remipassmoilesel on 13/12/16.
 */
public class Initialization {

    protected static final CustomLogger logger = LogManager.getLogger(Initialization.class);

    private static final String DEV_MODE_ARG = "--dev-mode";
    private static final String CREATE_FAKE_ARG = "--create-fake-project";

    public static void doInit(String[] args) throws IOException, InvocationTargetException, InterruptedException {

        // parse arguments
        boolean devMode = false;
        boolean createFake = false;
        Path projectToOpen = null;

        if (args != null && args.length > 0) {
            for (String str : args) {

                // debug mode
                if (DEV_MODE_ARG.equalsIgnoreCase(str)) {
                    devMode = true;
                    System.out.println("Dev mode enabled");
                }

                // create fake project
                else if (CREATE_FAKE_ARG.equalsIgnoreCase(str)) {
                    createFake = true;
                    System.out.println("Fake project will be created");
                }

                // open project
                else {
                    projectToOpen = Paths.get(str);
                    System.out.println("Project will be open: " + str);
                }
            }
        }

        MainManager.setDebugMode(devMode);

        EventNotificationManager.setDebugMode(devMode);

        try {
            MainManager.init();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            LaunchError.showErrorAndDie();
        }

        // gui initialization
        GuiManager guim = MainManager.getGuiManager();
        guim.configureUiManager();
        GuiColors.init();

        SwingUtilities.invokeAndWait(() -> {
            guim.constructGui();
        });

        ProjectManager pman = MainManager.getProjectManager();

        // open specified project
        if (projectToOpen != null) {

            boolean opened = false;
            try {
                pman.openProject(projectToOpen);
                opened = true;
            } catch (IOException e) {
                logger.error(e);
            }

            // add it to recents files
            if (opened) {
                try {
                    MainManager.getRecentManager().addCurrentProject();
                    MainManager.getRecentManager().saveHistory();
                } catch (IOException e) {
                    logger.error(e);
                }
            }

        }
        // create a fake project
        else if (createFake) {
            pman.createFakeProject();
        }
        // or create a new project at launch
        else {
            pman.createNewProject();
        }

        // set default tool
        MainManager.getDrawManager().setCurrentTool(ToolLibrary.LINE_TOOL);

        // run gui initialization operation when all others operations started
        SwingUtilities.invokeAndWait(() -> {
            guim.runIntializationOperations();
        });


    }
}
