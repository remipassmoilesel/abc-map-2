package org.abcmap.core.configuration;

import com.labun.surf.Params;

import javax.swing.filechooser.FileSystemView;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Misc configuration constants
 */
public class ConfigurationConstants {

    /**
     * Root package of interaction elemente, for introspection search
     */
    public static final String IE_PACKAGE_ROOT = "org.abcmap.ielements";

    /**
     * Root for plugins
     */
    public static final String PLUGINS_PACKAGE_ROOT = "org.abcmap.plugins";

    /**
     * Packages where are stored draw
     */
    public static final String SOFTWARE_NAME = "Abc-Map";
    public static final String SOFTWARE_VERSION = "2.00";

    /**
     * Misc URL
     */
    public static final String WEBSITE_URL = "http://abc-map.fr/";
    public static final String WEBSITE_FAQ_URL = "http://abc-map.fr/faq/";
    public static final String PROJECT_PRES_PAGE_URL = WEBSITE_URL + "project/";
    public static final String VOTE_PAGE_URL = WEBSITE_URL + "vote/";
    public static final String NEWS_PAGE_URL = WEBSITE_URL + "news/";
    public static final String HELP_PAGE_URL = WEBSITE_URL + "help/";
    public static final String BUG_REPORT_URL = WEBSITE_URL + "contact/?action=report";
    public static final String ASK_FORM_URL = WEBSITE_URL + "contact/?action=ask";

    public static final String DISTANT_WMS_SERVERS_LIST_URL = "http://localhost/servers.json";

    /*
     * Print settings
     */
    public static final Float JAVA_RESOLUTION = 72f;
    public static final Integer DEFAULT_PRINT_RESOLUTION = 300;


    /*
     * Main paths of software
     */
    public static String SYSTEM_HOME_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
    public static final Path SYMBOLS_DIR_PATH = Paths.get("./symbols");
    public static final Path DATA_DIR_PATH = Paths.get("./data");
    public static final Path CONFIGURATION_ROOT_PATH = Paths.get("./config");
    public static final Path LOG_DIRECTORY = Paths.get("./log");
    public static final Path TEMP_FOLDER = Paths.get("./tmp");
    public static final Path HELP_DIR = Paths.get("./help");

    /*
     *
     * HMI settings
     *
     */

    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
    public static final int SCROLLBAR_UNIT_INCREMENT = 20;

    /**
     * Locale list of WMS servers which can be used to add WMS Layers
     */
    public static final String LOCAL_WMS_SERVER_LIST = "/servers.json";


    /*
     * Configuration profiles settings
     */
    public static final String PROFILE_EXTENSION = "xml";
    public static final Path DEFAULT_PROFILE_PATH = Paths.get(ConfigurationConstants.CONFIGURATION_ROOT_PATH.toString(), "default." + PROFILE_EXTENSION);
    public static final Path CURRENT_PROFILE_PATH = Paths.get(ConfigurationConstants.CONFIGURATION_ROOT_PATH.toString(), ".current.xml");

    /**
     * Path where history  of projects and profiles is stored
     */
    public static final Path HISTORY_PATH = Paths.get(ConfigurationConstants.CONFIGURATION_ROOT_PATH.toString(), ".history.xml");

    public static final String PROJECT_EXTENSION = "abm";

    public static final String DEFAULT_LANGUAGE = "fr_FR";

    public static final int BACKUP_INTERVAL = 2000 * 60;

    /**
     * Upper case mandatory
     */
    public static final String SQL_TABLE_PREFIX = "ABM_";

    public static final Params[] SURF_PARAMS = new Params[]{
            new Params(4, 4, 0.01f, 2, false, false, false, 1, false),
            new Params(4, 4, 0.001f, 2, false, false, false, 1, false),
            new Params(4, 4, 0.0001f, 2, false, false, false, 1, false),
            new Params(4, 4, 0.00001f, 2, false, false, false, 1, false),
            new Params(4, 4, 0.000001f, 2, false, false, false, 1, false),};


}