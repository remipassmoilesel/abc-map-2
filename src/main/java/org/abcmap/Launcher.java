package org.abcmap;


import org.abcmap.core.managers.MainManager;

import java.nio.file.Paths;

public class Launcher {

    public static void main(String[] args) {

        System.out.println("Initializing Abc-Map, please wait ...");
        System.out.println("Working directory: " + Paths.get(".").toAbsolutePath());
        System.out.println();

        try {

            // init software
            Initialization.doInit(args);

            // show gui
            MainManager.getGuiManager().showGui();

        } catch (Throwable e) {
            LaunchError.showErrorAndDie(e);
        }

    }

}
