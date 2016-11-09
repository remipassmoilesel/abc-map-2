package abcmap.gui.dialogs.simple;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import abcmap.gui.GuiIcons;
import abcmap.managers.Log;

public class SimpleErrorDialog extends SimpleInformationDialog {

	public static void showAndWait(final Window parent, final String message) {
		showAndWait(parent, "Erreur", message);
	}

	public static void showAndWait(final Window parent, final String title, final String message) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					SimpleErrorDialog sd = new SimpleErrorDialog(parent);
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

	public static void showLater(final Window parent, final String title, final String message) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SimpleErrorDialog sd = new SimpleErrorDialog(parent);
				sd.setTitle(title);
				sd.setMessage(message);
				sd.reconstruct();

				sd.setVisible(true);
			}
		});
	}

	public static void showLater(final Window parent, String message) {
		showLater(parent, "Erreur", message);
	}

	public SimpleErrorDialog(Window parent) {
		super(parent);

		largeIcon = GuiIcons.DIALOG_ERROR_ICON;
		reconstruct();
	}

	/**
	 * Affecter le message du dialog <br>
	 * + petite note sur opérations de rétablissement possibles
	 */
	@Override
	public void setMessage(String message) {
		message += "<br><br> Si cette erreur persiste vous pouvez enregistrer vos travaux et redémarrer"
				+ " le logiciel, ou consulter la documentation en ligne.";

		super.setMessage(message);
	}

}