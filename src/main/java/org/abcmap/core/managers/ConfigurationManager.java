package org.abcmap.core.managers;

import com.labun.surf.Params;
import com.thoughtworks.xstream.XStream;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.configuration.ConfigurationContainer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manage configuration of software. Here you can store and use settings.
 */
public class ConfigurationManager {

    private final XStream xmlSerializer;
    private ConfigurationContainer currentConfiguration;

    ConfigurationManager() throws IOException {
        // load default configuration
        this.currentConfiguration = new ConfigurationContainer();
        this.xmlSerializer = new XStream();

        initializeConfiguration();
    }

    private void initializeConfiguration() throws IOException {

        // check configuration directory
        Path root = ConfigurationConstants.PROFILE_ROOT_PATH;
        if (Files.isDirectory(root) == false) {
            Files.createDirectories(root);
        }

        // check default profile if necesary
        Path defaultProfile = ConfigurationConstants.DEFAULT_PROFILE_PATH;
        if (Files.isRegularFile(defaultProfile) == false) {
            saveConfiguration(new ConfigurationContainer(), defaultProfile);
        }

        // check current profile
        Path currentProfile = ConfigurationConstants.CURRENT_PROFILE_PATH;
        if (Files.isRegularFile(currentProfile) == true) {
            loadConfiguration(currentProfile);
        } else {
            ConfigurationContainer config = new ConfigurationContainer();
            saveConfiguration(config, currentProfile);
            loadConfiguration(config);
        }


    }

    /**
     * Save a configuration container to the specified location.
     *
     * @param container
     * @param destination
     * @throws IOException
     */
    public void saveConfiguration(ConfigurationContainer container, Path destination) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(destination, ConfigurationConstants.DEFAULT_CHARSET)) {
            writer.write(xmlSerializer.toXML(container));
            writer.flush();
            writer.close();
        }

    }

    /**
     * Load and return a configuration container from the specified source.
     *
     * @param source
     * @return
     * @throws IOException
     */
    public ConfigurationContainer loadConfiguration(Path source) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(source, ConfigurationConstants.DEFAULT_CHARSET)) {
            ConfigurationContainer config = (ConfigurationContainer) xmlSerializer.fromXML(reader);
            return loadConfiguration(config);
        }
    }

    private ConfigurationContainer loadConfiguration(ConfigurationContainer config) {
        this.currentConfiguration = config;
        return currentConfiguration;
    }

    public ConfigurationContainer getConfiguration() {
        return currentConfiguration;
    }


    /**
     * Return the current SURF configuration.
     *
     * @return
     */
    public Params getSurfConfiguration() {

        // check if current parameter is valid
        if (currentConfiguration.IMPORT_SURF_MODE < 0
                || currentConfiguration.IMPORT_SURF_MODE > ConfigurationConstants.SURF_PARAMS.length - 1) {
            currentConfiguration.IMPORT_SURF_MODE = 0;
        }

        return ConfigurationConstants.SURF_PARAMS[currentConfiguration.IMPORT_SURF_MODE];

    }


}
