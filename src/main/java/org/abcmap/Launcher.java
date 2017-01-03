package org.abcmap;


import org.abcmap.core.managers.Main;

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
            Main.getGuiManager().showGui();

        } catch (Throwable e) {
            LaunchError.showErrorAndDie(e);
        }

    }

}
