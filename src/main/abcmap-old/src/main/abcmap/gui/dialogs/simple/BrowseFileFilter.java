package abcmap.gui.dialogs.simple;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import abcmap.configuration.ConfigurationConstants;
import abcmap.utils.Utils;

public class BrowseFileFilter extends FileFilter {

	/**
	 * Filtre d'images
	 */
	public static BrowseFileFilter PICTURES_FILEFILTER = new BrowseFileFilter(
			new String[] { "jpg", "jpeg", "bmp", "png", "tiff" }, "Images");

	/**
	 * Filtre de projet
	 */
	public static BrowseFileFilter PROJECTS_FILEFILTER = new BrowseFileFilter(ConfigurationConstants.PROJECT_EXTENSION,
			"*." + ConfigurationConstants.PROJECT_EXTENSION);

	/**
	 * Filtre de profil
	 */
	public static BrowseFileFilter PROFILES_FILEFILTER = new BrowseFileFilter(ConfigurationConstants.PROFILE_EXTENSION,
			"*." + ConfigurationConstants.PROFILE_EXTENSION);

	private String description;
	private String[] extensions;

	public BrowseFileFilter(String extension, String description) {
		this(new String[] { extension }, description);
	}

	public BrowseFileFilter(String[] extensions, String description) {
		this.extensions = extensions;
		this.description = description;
	}

	@Override
	public boolean accept(File f) {

		// accepter les dossier pour autoriser la navigation
		if (f.isDirectory())
			return true;

		// tester les extensions disponibles
		else {
			for (String ext1 : extensions) {
				String ext2 = Utils.getExtension(f.getAbsolutePath());
				if (Utils.safeEqualsIgnoreCase(ext1, ext2))
					return true;
			}
		}

		// ou retour
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
