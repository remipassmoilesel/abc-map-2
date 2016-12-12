package org.abcmap;


import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.gui.GuiColors;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) throws IOException {

        System.out.println("Initializing Abc-Map, please wait ...");
        System.out.println("Lancement d'Abc-Map, veuillez patienter...");

        try {
            MainManager.init();
        } catch (Exception e) {
            LaunchError.showErrorAndDie();
        }

        GuiManager guim = MainManager.getGuiManager();
        guim.configureUiManager();

        GuiColors.init();

        ProjectManager pman = MainManager.getProjectManager();

        // create a new project at launch
        pman.createNewProject();

        try {
            MainManager.getGuiManager().constructAndShowGui();
        } catch (Exception e) {
            LaunchError.showErrorAndDie(e);
        }

    }

}
