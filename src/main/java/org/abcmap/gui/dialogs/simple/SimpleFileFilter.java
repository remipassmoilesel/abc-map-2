package org.abcmap.gui.dialogs.simple;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.utils.Utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * File filters used in browse dialog
 */
public class SimpleFileFilter extends FileFilter {

    /**
     * File filter used to select images
     */
    public static SimpleFileFilter PICTURES_FILEFILTER = new SimpleFileFilter(
            new String[]{"jpg", "jpeg", "bmp", "png", "tiff"}, "Images");

    /**
     * File filter used to select Abc-Map projects
     */
    public static SimpleFileFilter PROJECTS_FILEFILTER = new SimpleFileFilter(ConfigurationConstants.PROJECT_EXTENSION,
            "*." + ConfigurationConstants.PROJECT_EXTENSION);

    /**
     * File filter used to select Abc-Map profiles
     */
    public static SimpleFileFilter PROFILES_FILEFILTER = new SimpleFileFilter(ConfigurationConstants.PROFILE_EXTENSION,
            "*." + ConfigurationConstants.PROFILE_EXTENSION);

    /**
     * Little description of filter, shown in browse dialog
     */
    private String description;

    /**
     * List of accepted extensions
     */
    private List<String> extensions;

    public SimpleFileFilter(String extension, String description) {
        this(new String[]{extension}, description);
    }

    public SimpleFileFilter(List<String> extensions, String description) {
        this.extensions = extensions;
        this.description = description;
    }

    public SimpleFileFilter(String[] extensions, String description) {
        this(Arrays.asList(extensions), description);
    }


    @Override
    public boolean accept(File f) {

        // display directories
        if (f.isDirectory()) {
            return true;
        }

        // display was provided
        return extensions.contains(Utils.getExtension(f.getAbsolutePath()).trim().toLowerCase());

    }

    @Override
    public String getDescription() {
        return description;
    }

}
