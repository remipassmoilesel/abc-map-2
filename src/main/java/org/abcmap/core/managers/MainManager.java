package org.abcmap.core.managers;

import org.abcmap.core.log.Logger;

/**
 * Main manager.
 * <p>
 * From here, all other managers can be reached.
 */
public class MainManager {

    private static final Logger log = Logger.getLogger(MainManager.class);
    private static ConfigurationManager configurationManager = null;

    /**
     * Initialize all managers
     */
    public static void init() {

        if (configurationManager != null) {
            log.warning("Main manager already initialized");
            return;
        }

        configurationManager = new ConfigurationManager();

    }

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

}
