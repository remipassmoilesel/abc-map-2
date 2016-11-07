package abcmap.gui.ie.project;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTextField;

import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;
import abcmap.utils.gui.TextFieldDelayedAction;
import net.miginfocom.swing.MigLayout;

public class SetProjectTitle extends InteractionElement {

	private ProjectListener projectListener;
	private JTextField textField;

	public SetProjectTitle() {

		this.label = Lng.get("set project title");
		this.help = Lng.get("set project title help");

	}

	@Override
	protected Component createPrimaryGUI() {

		// le panneau principal
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// le champs de texte
		textField = new JTextField();
		panel.add(textField, "width 80%, grow");

		// objet de mise a jour du champs de texte
		projectListener = new ProjectListener();

		// ecouter le gestionnaire de projets pour mise à jour du champs de
		// texte
		notifm.setDefaultUpdatableObject(projectListener);
		projectm.getNotificationManager().addObserver(this);

		// ecouter les changements avec actionretardée
		TextFieldDelayedAction.delayedActionFor(textField,
				new TextFieldListener(), false);

		projectListener.run();

		return panel;
	}

	/**
	 * Mets à jour le champs de texte
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ProjectListener extends FormUpdater {

		@Override
		public void updateFields() {

			if (projectm.isInitialized() == false) {
				GuiUtils.changeText(textField, "");
				return;
			}

			// titre du profil
			String projectTitle = projectm.getMetadatas().PROJECT_TITLE;

			GuiUtils.changeText(textField, projectTitle);
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

			// titre du profil
			String projectTitle = projectm.getMetadatas().PROJECT_TITLE;

			// titre du champs de texte
			String fieldTitle = textField.getText();

			if (Utils.safeEquals(projectTitle, fieldTitle) == false) {
				projectm.getMetadatas().PROJECT_TITLE = fieldTitle;
				projectm.fireMetadatasChanged();
			}

		}

	}

}
