package org.abcmap;


import org.abcmap.core.managers.MainManager;
import org.h2.tools.Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Launcher {

    public static void main(String[] args) throws IOException, SQLException {

        System.out.println("Initializing Abc-Map, please wait ...");
        System.out.println("Working directory: " + Paths.get(".").toAbsolutePath());
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
