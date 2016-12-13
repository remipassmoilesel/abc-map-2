package org.abcmap;


import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.gui.GuiColors;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {

    private static final String PANDA_MODE_ARG = "--panda-mode";
    private static final String CREATE_FAKE_ARG = "--create-fake-project";

    public static void main(String[] args) throws IOException {

        System.out.println("Initializing Abc-Map, please wait ...");
        System.out.println();

        // parse arguments
        boolean devMode = false;
        boolean createFake = false;
        Path projectToOpen = null;
        if (args != null && args.length > 0) {
            for (String str : args) {
                if (PANDA_MODE_ARG.equalsIgnoreCase(str)) {
                    devMode = true;
                    System.out.println("Panda mode activated");
                } else if (CREATE_FAKE_ARG.equalsIgnoreCase(str)) {
                    createFake = true;
                    System.out.println("Fake project will be created");
                } else {
                    projectToOpen = Paths.get(str);
                    System.out.println("Project will be opened: " + str);
                }
            }

        }

        MainManager.setDebugMode(devMode);

        EventNotificationManager.setDebugMode(devMode);

        try {
            MainManager.init();
        } catch (Exception e) {
            LaunchError.showErrorAndDie();
        }

        // gui initialization
        GuiManager guim = MainManager.getGuiManager();
        guim.configureUiManager();
        GuiColors.init();

        ProjectManager pman = MainManager.getProjectManager();

        // open specified project
        if (projectToOpen != null) {
            pman.openProject(projectToOpen);
        }
        // create a fake project
        else if (createFake) {
            pman.createFakeProject();
        }
        // or create a new project at launch
        else {
            pman.createNewProject();
        }

        try {
            MainManager.getGuiManager().constructAndShowGui();
        } catch (Exception e) {
            LaunchError.showErrorAndDie(e);
        }

    }

}
