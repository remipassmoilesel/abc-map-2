package org.abcmap.gui.dialogs.simple;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.utils.Utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filters used in browse dialog
 */
public class BrowseFileFilter extends FileFilter {

    public static BrowseFileFilter PICTURES_FILEFILTER = new BrowseFileFilter(
            new String[]{"jpg", "jpeg", "bmp", "png", "tiff"}, "Images");

    public static BrowseFileFilter PROJECTS_FILEFILTER = new BrowseFileFilter(ConfigurationConstants.PROJECT_EXTENSION,
            "*." + ConfigurationConstants.PROJECT_EXTENSION);

    public static BrowseFileFilter PROFILES_FILEFILTER = new BrowseFileFilter(ConfigurationConstants.PROFILE_EXTENSION,
            "*." + ConfigurationConstants.PROFILE_EXTENSION);

    private String description;
    private String[] extensions;

    public BrowseFileFilter(String extension, String description) {
        this(new String[]{extension}, description);
    }

    public BrowseFileFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {

        // display directories
        if (f.isDirectory()) {
            return true;
        }

        // display files if extension is supported
        else {
            for (String ext1 : extensions) {
                String ext2 = Utils.getExtension(f.getAbsolutePath());
                if (Utils.safeEqualsIgnoreCase(ext1, ext2)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
