package abcmap.gui.dialogs.simple;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.gui.GuiIcons;
import abcmap.gui.dialogs.QuestionResult;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

/**
 * Boite de dialogue de question simple.
 * 
 * @author remipassmoilesel
 *
 */
public class SimpleQuestionDialog extends SimpleInformationDialog {

	public static QuestionResult askQuestion(Window parent, String message) {
		return askQuestion(parent, "Question", message);
	}

	public static QuestionResult askQuestion(Window parent, String title,
			String message) {

		GuiUtils.throwIfNotOnEDT();

		SimpleQuestionDialog sd = new SimpleQuestionDialog(parent);
		sd.setTitle(title);
		sd.setMessage(message);
		sd.reconstruct();
		sd.setVisible(true);

		return sd.getResult();
	}

	public static QuestionResult askQuestionAndWait(final Window parent,
			String title, final String message) {

		final QuestionResult result = new QuestionResult();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					result.update(askQuestion(parent, message));
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
		}

		return result;
	}

	/** Texte afficher sur le bouton oui */
	private String yesText;

	/** Texte afficher sur le bouton non */
	private String noText;

	/** Texte afficher sur le bouton annuler */
	private String cancelText;

	/** Le résultat à retourner */
	private QuestionResult result;

	public SimpleQuestionDialog(Window parent) {
		super(parent);

		// ecouter la fermeture
		addWindowListener(new ClosingWindowListener());

		// caractéristiques par défaut
		setModal(true);

		largeIcon = GuiIcons.DIALOG_QUESTION_ICON;

		this.yesText = "Oui";
		this.noText = "Non";
		this.cancelText = "Annuler";

		result = new QuestionResult();

		// construction
		reconstruct();
	}

	/**
	 * Retourne un JPanel avec les boutons disponibles. <br>
	 * A surcharger pour modifier les boutons.
	 */
	@Override
	protected void addDefaultButtons() {

		buttonsPanel = new JPanel(new MigLayout());

		// bouton ok
		JButton buttonYes = new JButton(yesText);
		buttonYes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result.setReturnVal(QuestionResult.YES);
				dispose();
			}
		});
		buttonsPanel.add(buttonYes);

		// bouton non
		JButton buttonNo = new JButton(noText);
		buttonNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result.setReturnVal(QuestionResult.NO);
				dispose();
			}
		});
		buttonsPanel.add(buttonNo);

		// bouton annuler
		JButton buttonCancel = new JButton(cancelText);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result.setReturnVal(QuestionResult.CANCEL);
				dispose();
			}
		});
		buttonsPanel.add(buttonCancel);

		// ajouter au contenu
		contentPane.add(buttonsPanel, "align right, gapright 15px, wrap 15px,");

	}

	/**
	 * L'utilisateur ferme, la réponse sera annulé
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ClosingWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			result.setReturnVal(QuestionResult.CANCEL);
			dispose();
		}
	}

	public void setYesText(String okText) {
		this.yesText = okText;
	}

	public void setNoText(String noText) {
		this.noText = noText;
	}

	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}

	public QuestionResult getResult() {
		return result;
	}

}
