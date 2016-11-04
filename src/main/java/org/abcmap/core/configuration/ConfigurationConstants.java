package org.abcmap.core.configuration;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Misc configuration constants
 */
public class ConfigurationConstants {

    /**
     * Root for interaction element introspection search
     */
    public static final String IE_PACKAGE_ROOT = "org.abcmap.gui.ie";

    /**
     * Root for plugins
     */
    public static final String PLUGINS_PACKAGE_ROOT = "org.abcmap.plugins";

    /**
     * Packages where are stored shapes
     */
    public final static String DRAW_PACKAGE = "abcmap.draw.shapes";

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
    public static final String BUG_REPORT_URL = WEBSITE_URL
            + "contact/?action=report";
    public static final String ASK_FORM_URL = WEBSITE_URL
            + "contact/?action=ask";

    /*
     * Print settings
     */
    public static final Float SCREEN_RESOLUTION = (float) Toolkit
            .getDefaultToolkit().getScreenResolution();
    public static final Float JAVA_RESOLUTION = 72f;
    public static final Integer DEFAULT_PRINT_RESOLUTION = 300;

    /*
     * Main paths of software
     */
    public static String SYSTEM_HOME_PATH = FileSystemView.getFileSystemView()
            .getDefaultDirectory().getAbsolutePath();
    public static final String SYMBOLS_DIR_PATH = "./symbols/";
    public static final String PROFILE_ROOT_PATH = "./profiles/";
    public static final String LOG_DIRECTORY = "./log/";
    public static final String HELP_DIR = "./help/";

    /*
     *
     * HMI settings
     *
     */

    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
    public static final int SCROLLBAR_UNIT_INCREMENT = 20;
    public static final Dimension MINIMUM_MAP_DIMENSIONS = new Dimension(800,
            600);

    /*
     * Configuration profiles settings
     */

    public static final String PROFILE_EXTENSION = "prf";
    public static final String DEFAULT_PROFILE_PATH = ConfigurationConstants.PROFILE_ROOT_PATH
            + "default." + PROFILE_EXTENSION;
    public static final String SYSTEM_PROFILE_PATH = ConfigurationConstants.PROFILE_ROOT_PATH
            + "system";

    public static final String XML_PARAMETER_TAG = "parameter";
    public static final String XML_ROOT_NAME = "parameters";
    public static final String XML_PARAMETER_ATTRIBUTE_NAME = "name";

    public static final String PROJECT_EXTENSION = "abm";

    public static final String DEFAULT_LANGUAGE = "fr_FR";

}