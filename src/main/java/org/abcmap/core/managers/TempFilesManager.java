package org.abcmap.core.managers;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.io.FileUtils.getTempDirectoryPath;
import static sun.management.Agent.error;

/**
 * Manage temporary files and folders
 */
public class TempFilesManager {

    private static final CustomLogger logger = LogManager.getLogger(TempFilesManager.class);
    private final Path tempFolder;

    public TempFilesManager() throws IOException {

        // check if temp folder exist
        this.tempFolder = ConfigurationConstants.TEMP_FOLDER;

        if(Files.exists(tempFolder) == false){
            Files.createDirectories(tempFolder);
        }

    }

    /**
     * Create a temproray file with specified prefix and suffix
     *
     * @param prefix
     * @param suffix
     * @return
     */
    public Path createTemporaryFile(String prefix, String suffix) throws IOException{

        // prefix is mandatory
        if (prefix == null) {
            throw new NullPointerException();
        }

        if (suffix == null) {
            suffix = "";
        }

        Path file = null;

        do{
            String name = prefix + System.nanoTime() + suffix;
            file = Paths.get(getTempDirectoryPath(), name);
        } while(Files.exists(file));

        Files.createFile(file);

        return file;
    }

    /**
     * Create a project temporary folder
     *
     * @return
     * @throws IOException
     */
    public Path createProjectTempDirectory() throws IOException {

        Path finalPath;

        // generate a non existent path
        int i = 0;
        String name;
        String complement = "";

        do {
            if(i == 0){
                complement = "";
            }
            else {
                complement = "_" + i;
            }
            name = new SimpleDateFormat("yyyy-M-d-HH-mm").format(new Date()) + complement;
            finalPath = Paths.get(ConfigurationConstants.TEMP_FOLDER.toString(), name);
            i++;
        } while (Files.exists(finalPath));

        // create directory
        Files.createDirectories(finalPath);

        return finalPath;
    }

}
