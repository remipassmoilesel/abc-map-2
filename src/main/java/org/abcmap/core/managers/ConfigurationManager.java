package org.abcmap.core.managers;

import com.labun.surf.Params;
import com.thoughtworks.xstream.XStream;
import org.abcmap.core.configuration.CFNames;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.configuration.ConfigurationContainer;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manage configuration of software. Here you can store and use settings.
 */
public class ConfigurationManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private final XStream xmlSerializer;
    private final EventNotificationManager notifm;
    private ConfigurationContainer currentConfiguration;

    public ConfigurationManager() throws IOException {
        // load default configuration
        this.currentConfiguration = new ConfigurationContainer();
        this.xmlSerializer = new XStream();

        this.notifm = new EventNotificationManager(ConfigurationManager.this);

        initializeConfiguration();
    }

    private void initializeConfiguration() throws IOException {

        // check configuration directory
        Path root = ConfigurationConstants.CONFIGURATION_ROOT_PATH;
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

    /**
     * Load a configuration object and keep current configuration
     *
     * @param config
     * @return
     */
    private ConfigurationContainer loadConfiguration(ConfigurationContainer config) {
        this.currentConfiguration = config;
        return currentConfiguration;
    }

    /**
     * Get current configuration
     *
     * @return
     */
    public ConfigurationContainer getConfiguration() {
        return currentConfiguration;
    }


    /**
     * Return the current SURF configuration.
     *
     * @return
     */
    public Params getSurfConfiguration() {

        Integer surfMode = currentConfiguration.getInt(CFNames.IMPORT_SURF_MODE);

        // check if current parameter is valid
        if (surfMode < 0 || surfMode > ConfigurationConstants.SURF_PARAMS.length - 1) {
            surfMode = 0;
            currentConfiguration.updateValue(CFNames.IMPORT_SURF_MODE, surfMode);
        }

        return ConfigurationConstants.SURF_PARAMS[surfMode];

    }


    /**
     * Get current SURF analyse mode
     *
     * @return
     */
    public int getSurfMode() {
        return currentConfiguration.getInt(CFNames.IMPORT_SURF_MODE);
    }

    /**
     * Set current SURF analyse mode
     *
     * @param surfMode
     */
    public void setSurfMode(int surfMode) {

        // check interval
        if (surfMode < 0 || surfMode > ConfigurationConstants.SURF_PARAMS.length) {
            throw new IllegalArgumentException("Invalid SURF mode. It should be greater than 0 and less than "
                    + ConfigurationConstants.SURF_PARAMS.length + ": " + surfMode);
        }

        currentConfiguration.updateValue(CFNames.IMPORT_SURF_MODE, surfMode);
    }


    public Rectangle getCropRectangle() {
        return new Rectangle();
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    public void setCropRectangle(Rectangle cropRectangle) {
    }

    public boolean isCroppingEnabled() {
        return true;
    }

    public void setCroppingEnabled(boolean croppingEnabled) {

    }

    public long getWindowHidingDelay() {
        return 0;
    }


    public boolean isSaveProfileWhenQuit() {
        return false;
    }

    public void saveCurrentProfile() throws IOException {
        saveConfiguration(currentConfiguration, Paths.get(currentConfiguration.getValue(CFNames.PROFILE_PATH)));
    }
}
