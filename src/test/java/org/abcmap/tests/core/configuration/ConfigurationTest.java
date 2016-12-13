package org.abcmap.tests.core.configuration;

import org.abcmap.TestUtils;
import org.abcmap.core.configuration.ConfigurationContainer;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.MainManager;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertTrue;

public class ConfigurationTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void configurationTest() throws IOException {

        ConfigurationManager configm = MainManager.getConfigurationManager();

        // basic equality test
        assertTrue("Equality test", configm.getConfiguration().equals(configm.getConfiguration()));

        // prepare temp files
        Path tempDir = TestUtils.PLAYGROUND_DIRECTORY.resolve("configurationPersistenceTest");
        FileUtils.deleteDirectory(tempDir.toFile());
        Files.createDirectories(tempDir);

        // create default configuration
        Path configurationPath = tempDir.resolve("configuration.xml");
        ConfigurationContainer defaultConfiguration = new ConfigurationContainer();

        // change configuration
        defaultConfiguration.DEFAULT_LANGUAGE = "bloubi";
        defaultConfiguration.HOME = "/home/bloubiboy";

        // save configuration
        configm.saveConfiguration(defaultConfiguration, configurationPath);
        assertTrue("Save configuration", Files.exists(configurationPath));

        // load and compare
        ConfigurationContainer loadedConfig = configm.loadConfiguration(configurationPath);
        assertTrue("Load configuration", loadedConfig.equals(defaultConfiguration));

    }

}
