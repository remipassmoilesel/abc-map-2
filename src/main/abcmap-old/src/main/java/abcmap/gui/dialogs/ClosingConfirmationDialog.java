package abcmap.gui.dialogs;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import abcmap.gui.dialogs.simple.SimpleQuestionDialog;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class ClosingConfirmationDialog extends SimpleQuestionDialog {

	public static enum ConfirmationType {
		PROJECT, PROFILE,
	}

	/**
	 * Affiche un message de confirmation de fermeture
	 * 
	 * @return
	 */
	public static QuestionResult showProjectConfirmation(Window parent) {

		// pas de lancement hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// creer puis afficher le dialogue
		ClosingConfirmationDialog ccd = new ClosingConfirmationDialog(parent,
				ConfirmationType.PROJECT);
		ccd.setVisible(true);

		// retourner le resultat
		return ccd.getResult();
	}

	public static QuestionResult showProjectConfirmationAndWait(
			final Window parent) {

		final QuestionResult result = new QuestionResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(showProjectConfirmation(parent));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;

	}

	/**
	 * Affiche un message de confirmation de fermeture
	 * 
	 * @return
	 */
	public static QuestionResult showProfileConfirmation(Window parent) {

		// pas de lancement hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// creer puis afficher le dialogue
		ClosingConfirmationDialog ccd = new ClosingConfirmationDialog(parent,
				ConfirmationType.PROFILE);
		ccd.setVisible(true);

		// retourner le resultat
		return ccd.getResult();

	}

	public static QuestionResult showProfileConfirmationAndWait(
			final Window parent) {

		final QuestionResult result = new QuestionResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(showProfileConfirmation(parent));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
			return null;
		}

		return result;

	}

	public ClosingConfirmationDialog(Window parent, ConfirmationType type) {
		super(parent);

		// question
		if (ConfirmationType.PROJECT.equals(type)) {
			setMessage("Voulez vous enregistrer le projet courant ?");
		}

		else if (ConfirmationType.PROFILE.equals(type)) {
			setMessage("Voulez vous enregistrer le profil de configuration courant ?");
		}

		// boutons
		setYesText("Enregistrer");
		setNoText("Ne pas enregistrer");
		setCancelText("Annuler l'opération en cours");

		reconstruct();
	}
}
