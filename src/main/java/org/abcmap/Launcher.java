package org.abcmap;


import com.j256.ormlite.logger.LocalLog;
import org.abcmap.core.configuration.ConfigurationContainer;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;

import java.io.IOException;
import java.nio.file.Paths;

public class Launcher {

    public static void main(String[] args) throws IOException {

        System.out.println("Initializing Abc-Map, please wait ...");
        System.out.println("Lancement d'Abc-Map, veuillez patienter...");

        try{
            MainManager.init();
        } catch (Exception e){
            LaunchError.showErrorAndDie();
        }

        ProjectManager pman = MainManager.getProjectManager();

        // create a new project at launch
        pman.createNewProject();


    }

}
