package org.abcmap.core.managers;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Manage temporary files and folders
 * <p>
 * // TODO: Merge with ProjectManager ?
 */
public class TempFilesManager {

    private static final CustomLogger logger = LogManager.getLogger(TempFilesManager.class);
    private final Path tempFolderPath;

    public TempFilesManager() throws IOException {

        // check if temp folder exist
        this.tempFolderPath = ConfigurationConstants.TEMP_FOLDER;

        if (Files.exists(tempFolderPath) == false) {
            Files.createDirectories(tempFolderPath);
        }

    }

    /**
     * Create a temporary file with specified name and suffix
     *
     * @param prefix
     * @param suffix
     * @return
     */
    public Path createTemporaryFile(String prefix, String suffix) throws IOException {

        ProjectManager projectm = Main.getProjectManager();
        if (projectm.isInitialized() == false) {
            throw new IllegalStateException("Project not initialized");
        }

        // prefix is mandatory
        if (prefix == null) {
            throw new NullPointerException("Prefix is mandatory");
        }

        if (suffix == null) {
            suffix = "";
        }

        Path file = null;

        do {
            String name = prefix + System.nanoTime() + suffix;
            file = projectm.getProject().getTempDirectory().resolve(name);
        } while (Files.exists(file));

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
        return createTempDirectory(ConfigurationConstants.TEMP_FOLDER);
    }

    /**
     * Create a temporary directory at specified root
     *
     * @param root
     * @return
     * @throws IOException
     */
    public Path createTempDirectory(Path root) throws IOException {

        Path finalPath;

        if (root == null) {
            root = Paths.get(".");
        }

        // generate a non existent path
        int i = 0;
        String name;
        String complement = "";

        do {
            if (i == 0) {
                complement = "";
            } else {
                complement = "_" + i;
            }
            name = new SimpleDateFormat("yyyy-M-d-HH-mm").format(new Date()) + complement;
            finalPath = root.resolve(name);
            i++;
        } while (Files.exists(finalPath));

        // create directory
        Files.createDirectories(finalPath);

        return finalPath;
    }

    /**
     * Delete a temp file recursively
     *
     * @param tempPath
     * @throws IOException
     */
    public void deleteTempFile(Path tempPath) throws IOException {
        Utils.deleteDirectories(tempPath.toFile());
    }

    /**
     * Return the list of files from current temporary folder
     *
     * @return
     */
    public ArrayList<Path> getTemporaryFilesFromCurrentProject() {

        ArrayList<Path> results = new ArrayList<>();
        for (Path p : Main.getProjectManager().getProject().getTempDirectory()) {
            results.add(p);
        }

        return results;
    }
}
