package org.abcmap.core.configuration;

import org.abcmap.TestConstants;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.MainManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertTrue;

public class ConfigurationTest {

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

        Path configurationDir = TestConstants.PLAYGROUND_DIRECTORY.resolve("configurationTest");
        Files.createDirectories(configurationDir);

        Path configurationPath = configurationDir.resolve("configuration.xml");

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
