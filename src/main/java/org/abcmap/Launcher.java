package org.abcmap;


import org.abcmap.core.configuration.ConfigurationContainer;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.MainManager;

import java.io.IOException;
import java.nio.file.Paths;

public class Launcher {

    public static void main(String[] args) throws IOException {

        System.out.println("Initializing Abc-Map, please wait ...");

        try{
            throw new Exception();
//            MainManager.init();
        } catch (Exception e){
            LaunchError.showErrorAndDie();
        }


    }

}
