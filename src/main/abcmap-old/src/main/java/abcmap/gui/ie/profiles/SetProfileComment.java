package abcmap.gui.ie.profiles;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import abcmap.gui.ie.InteractionElement;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;
import abcmap.utils.gui.TextFieldDelayedAction;
import net.miginfocom.swing.MigLayout;

public class SetProfileComment extends InteractionElement {

	private ProfileListener profileListener;
	private JTextArea textField;

	public SetProfileComment() {
		label = Lng.get("set profile comment");
		help = Lng.get("set profile comment help");
	}

	@Override
	protected Component createPrimaryGUI() {

		// le panneau principal
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// le champs de texte
		textField = new JTextArea();

		// dans un scroollpane
		panel.add(new JScrollPane(textField), "width 80%, height 70px, grow");

		// objet de mise a jour du champs de texte
		profileListener = new ProfileListener();

		// ecouter le gestionnaire de projets pour mise à jour du champs de
		// texte
		notifm.setDefaultUpdatableObject(profileListener);
		configm.getNotificationManager().addObserver(this);

		// ecouter les changements
		TextFieldDelayedAction.delayedActionFor(textField,
				new CommentListener(), false);

		// initialisation
		profileListener.run();

		return panel;

	}

	/**
	 * Met à jour le champs de texte en fonction du profil
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ProfileListener extends FormUpdater {

		@Override
		public void updateFields() {

			// projet non initialisé, retour
			if (projectm.isInitialized() == false) {
				GuiUtils.changeText(textField, "");
				return;
			}

			// commentaire du profil
			String profileComment = configm.getConfiguration().PROFILE_COMMENT;

			GuiUtils.changeText(textField, profileComment);
		}

	}

	/**
	 * Met à jour le projet en fonction du champs de texte.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CommentListener implements Runnable {

		@Override
		public void run() {

			// Verifier le projet et obtenir le calque actif, ou afficher un
			// message d'erreur
			MapLayer layer = checkProjectAndGetActiveLayer();
			if (layer == null) {
				return;
			}

			// commentaire du projet
			String projectComment = configm.getConfiguration().PROFILE_COMMENT;

			// recuperer le titre saisi
			String fieldComment = textField.getText();

			if (Utils.safeEquals(projectComment, fieldComment) == false) {
				configm.getConfiguration().PROFILE_COMMENT = fieldComment;
				configm.fireParametersUpdated();
			}

		}

	}

}
