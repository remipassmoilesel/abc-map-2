package abcmap.gui.dialogs.simple;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.dialogs.QuestionResult;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class SimpleBrowseDialog {

	public static BrowseDialogResult browseDirectory(Window parent) {
		return browseDirectory(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "O.K.");
	}

	public static BrowseDialogResult browseDirectoryAndWait(final Window parent) {

		final BrowseDialogResult result = new BrowseDialogResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(browseDirectory(parent));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;
	}

	public static BrowseDialogResult browseProjectToSave(Window parent) {
		return browseFileToSave(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
	}

	public static BrowseDialogResult browseProjectToOpen(Window parent) {
		return browseFileToOpen(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
	}

	public static BrowseDialogResult browseProjectToOpenAndWait(final Window parent) {

		final BrowseDialogResult result = new BrowseDialogResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(browseProjectToOpen(parent));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;

	}

	public static BrowseDialogResult browseProfileToSave(Window parent) {
		return browseFileToSave(parent, BrowseFileFilter.PROFILES_FILEFILTER);
	}

	public static BrowseDialogResult browseProfileToSaveAndWait(final Window parent) {

		final BrowseDialogResult result = new BrowseDialogResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(browseProfileToSave(parent));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;

	}

	public static BrowseDialogResult browseProfileToOpen(Window parent) {
		return browseFileToOpen(parent, BrowseFileFilter.PROFILES_FILEFILTER);
	}

	public static BrowseDialogResult browseProfileToOpenAndWait(final Window parent) {

		final BrowseDialogResult result = new BrowseDialogResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(browseProfileToOpen(parent));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;
	}

	public static BrowseDialogResult browseFileToSave(Window parent, BrowseFileFilter filter) {
		return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Enregistrer", filter,
				true);
	}

	public static BrowseDialogResult browseFileToSaveAndWait(final Window parent,
			final BrowseFileFilter filter) {

		final BrowseDialogResult result = new BrowseDialogResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(browseFileToSave(parent, filter));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;
	}

	public static BrowseDialogResult browseFileToOpen(Window parent, BrowseFileFilter filter) {
		return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Ouvrir", filter, false);
	}

	public static BrowseDialogResult browseFileToOpenAndWait(final Window parent,
			final BrowseFileFilter filter) {

		final BrowseDialogResult result = new BrowseDialogResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent,
							"Ouvrir", filter, false));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;

	}

	public static BrowseDialogResult browseDirectory(String currentDirectoryPath, Component parent,
			String approveButtonText) {

		// execution uniquement sur EDT
		GuiUtils.throwIfNotOnEDT();

		// preparation de la boite de dialogue
		JFileChooser fc = new JFileChooser(currentDirectoryPath);
		fc.setDialogTitle("Parcourir");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// affichage
		int returnVal = fc.showDialog(parent, approveButtonText);

		// retour
		BrowseDialogResult bdr = new BrowseDialogResult();
		bdr.setFile(fc.getSelectedFile());
		bdr.setReturnVal(returnVal);

		return bdr;

	}

	public static BrowseDialogResult browseFile(String currentDirectoryPath, Window parent,
			String approveButtonText, BrowseFileFilter filter, boolean confirmOverwriting) {

		GuiUtils.throwIfNotOnEDT();

		// preparation de la boite de dialogue
		JFileChooser fc = new JFileChooser(currentDirectoryPath);
		fc.setDialogTitle("Parcourir");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// filtre
		if (filter != null) {
			fc.addChoosableFileFilter(filter);
		}

		int returnVal;
		boolean askAgain;
		do {

			askAgain = true;

			// affichage du dialog
			returnVal = fc.showDialog(parent, approveButtonText);

			// verifier si le fichier existe deja
			if (confirmOverwriting && fc.getSelectedFile() != null
					&& fc.getSelectedFile().isFile()) {
				QuestionResult result = SimpleQuestionDialog.askQuestion(parent,
						"Ecraser le fichier ?");
				if (result.getReturnVal().equals(QuestionResult.YES)) {
					askAgain = false;
				}
			} else {
				askAgain = false;
			}

		} while (askAgain);

		// retour
		BrowseDialogResult bdr = new BrowseDialogResult();
		bdr.setFile(fc.getSelectedFile());
		bdr.setReturnVal(returnVal);

		return bdr;

	}

}
