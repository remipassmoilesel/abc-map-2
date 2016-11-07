package abcmap.gui.ie.project;

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

public class SetProjectComment extends InteractionElement {

	private ProjectListener projectListener;
	private JTextArea textField;

	public SetProjectComment() {

		this.label = Lng.get("set project comment");
		this.help = Lng.get("set project comment help");

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
		projectListener = new ProjectListener();

		// ecouter le gestionnaire de projets pour mise à jour du champs de
		// texte
		notifm.setDefaultUpdatableObject(projectListener);
		projectm.getNotificationManager().addObserver(this);

		// ecouter les changements
		TextFieldDelayedAction.delayedActionFor(textField,
				new TextFieldListener(), false);

		// maj
		projectListener.run();

		return panel;

	}

	/**
	 * Mettre à jour le champs de texte en fonction du projet
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ProjectListener extends FormUpdater {

		@Override
		public void updateFields() {

			// projet non initialisé, retour
			if (projectm.isInitialized() == false) {
				GuiUtils.changeText(textField, "");
				return;
			}

			// commentaire du projet
			String projectComment = projectm.getMetadatas().PROJECT_COMMENT;

			GuiUtils.changeText(textField, projectComment);

		}

	}

	/**
	 * Ecoute les changements dans le champs texte et enregistre les valeurs
	 * saisies.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldListener implements Runnable {

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
				projectm.getMetadatas().PROJECT_COMMENT = fieldComment;
				projectm.fireMetadatasChanged();
			}

		}

	}

}
