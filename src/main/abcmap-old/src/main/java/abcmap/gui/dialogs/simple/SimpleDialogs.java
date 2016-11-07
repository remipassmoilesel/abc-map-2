package abcmap.gui.dialogs.simple;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import abcmap.managers.Log;

/**
 * Afficher des boite de dialogue simples.
 * 
 * @author remipassmoilesel
 *
 */
public class SimpleDialogs {

	public static void showInformationAndWait(final Window parent, String message) {
		showInformationAndWait(parent, "Information", message);
	}

	public static void showInformationAndWait(final Window parent, final String title,
			final String message) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SimpleInformationDialog sd = new SimpleInformationDialog(parent);
					sd.setTitle(title);
					sd.setMessage(message);
					sd.reconstruct();

					sd.setVisible(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
		}

	}

	public static void showInformationLater(final Window parent, final String title,
			final String message) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SimpleInformationDialog sd = new SimpleInformationDialog(parent);
				sd.setTitle(title);
				sd.setMessage(message);
				sd.reconstruct();

				sd.setVisible(true);
			}
		});
	}

	public static void showInformationLater(Window parent, String message) {
		showInformationLater(parent, "Information", message);
	}

}
