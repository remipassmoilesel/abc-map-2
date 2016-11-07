package abcmap.gui.ie.profiles;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTextField;

import abcmap.gui.ie.InteractionElement;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;
import abcmap.utils.gui.TextFieldDelayedAction;
import net.miginfocom.swing.MigLayout;

public class SetProfileTitle extends InteractionElement {

	private ProfileListener profileListener;
	private JTextField textField;

	public SetProfileTitle() {
		label = Lng.get("configuration profile title");
		help = Lng.get("configuration profile title help");

	}

	protected Component createPrimaryGUI() {

		// le panneau principal
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// le champs de texte
		textField = new JTextField();
		panel.add(textField, "width 80%, grow");

		// objet de mise a jour du champs de texte
		profileListener = new ProfileListener();

		// ecouter le gestionnaire de projets pour mise à jour du champs de
		// texte
		notifm.setDefaultUpdatableObject(profileListener);
		configm.getNotificationManager().addObserver(this);

		// ecouter les changements avec actionretardée
		TextFieldDelayedAction.delayedActionFor(textField,
				new TextFieldListener(), false);

		// initialisation
		profileListener.run();

		return panel;
	}

	/**
	 * Mettre à jour le champs de texte en fonction du profil
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

			// titre du profil
			String profileTitle = configm.getConfiguration().PROFILE_TITLE;

			GuiUtils.changeText(textField, profileTitle);

		}

	}

	/**
	 * Mettre à jour le profil en fonction du champs de texte
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

			// titre du profil
			String profileTitle = configm.getConfiguration().PROFILE_TITLE;

			// titre du champs de texte
			String fieldTitle = textField.getText();

			if (Utils.safeEquals(profileTitle, fieldTitle) == false) {
				configm.getConfiguration().PROFILE_TITLE = fieldTitle;
				configm.fireParametersUpdated();
			}

		}

	}

}
