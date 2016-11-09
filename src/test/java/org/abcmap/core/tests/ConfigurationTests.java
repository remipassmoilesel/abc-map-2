package org.abcmap.core.tests;

import org.abcmap.TestConstants;
import org.abcmap.core.configuration.ConfigurationContainer;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.MainManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.assertTrue;

public class ConfigurationTests {

    @BeforeClass
    public static void beforeTests() throws IOException {
        MainManager.init();
    }

    @Test
    public void configurationTest() throws IOException {

        ConfigurationManager config = MainManager.getConfigurationManager();

        assertTrue("Equality test", config.equals(config));

        /*
         * Save configuration
         */

        Path configurationPath = Paths.get(TestConstants.PLAYGROUND, "configuration.xml");

        ConfigurationContainer defaultConfiguration = new ConfigurationContainer();

        config.saveConfiguration(defaultConfiguration, configurationPath);

        assertTrue("Save configuration", Files.exists(configurationPath));

        /*
         * Load configuration
         */

        ConfigurationContainer loadedConfig = config.loadConfiguration(configurationPath);

        assertTrue("Load configuration", loadedConfig.equals(defaultConfiguration));

    }

}