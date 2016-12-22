package org.abcmap;


import org.abcmap.core.managers.MainManager;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) throws IOException {

        System.out.println("Initializing Abc-Map, please wait ...");
        System.out.println();

        try {

            // init software
            Initialization.doInit(args);

            // show gui
            MainManager.getGuiManager().showGui();

        } catch (Exception e) {
            LaunchError.showErrorAndDie(e);
        }

    }

}
