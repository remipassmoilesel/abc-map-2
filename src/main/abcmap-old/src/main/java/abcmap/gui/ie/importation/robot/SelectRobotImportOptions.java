package abcmap.gui.ie.importation.robot;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import abcmap.exceptions.InvalidInputException;
import abcmap.gui.comps.importation.RobotImportOptionsPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.importation.robot.RobotConfiguration;
import abcmap.managers.Log;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;

public class SelectRobotImportOptions extends InteractionElement {

	private RobotImportOptionsPanel robotOptionPanel;

	public SelectRobotImportOptions() {
		label = "Options de capture automatique";
		help = "...";
	}

	@Override
	protected Component createPrimaryGUI() {

		// l'element graphique
		robotOptionPanel = new RobotImportOptionsPanel();

		// ecouter la saisie utilisateur
		robotOptionPanel.getListenerHandler().add(new RobotConfigurationUpdater());

		// mise à jour du panneau à partir du gestionnaire de configuration
		configm.getNotificationManager().addObserver(this);
		notifm.setDefaultUpdatableObject(new PanelUpdater());

		return robotOptionPanel;

	}

	/**
	 * Met à jour la configuration en fonction des parametres saisis dans le
	 * formulaire
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class RobotConfigurationUpdater implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				RobotConfiguration panelValues = robotOptionPanel.getValues();
				if (configm.getRobotConfiguration().equals(panelValues) == false) {
					configm.setRobotConfiguration(panelValues);
				}
			} catch (InvalidInputException e1) {
				// Log.debug(e1);
			}
		}

	}

	private class PanelUpdater implements UpdatableByNotificationManager {

		@Override
		public void notificationReceived(Notification arg) {

			// récuperer la configuration actuelle
			final RobotConfiguration currentConfig = configm.getRobotConfiguration();

			try {
				RobotConfiguration panelConfig = robotOptionPanel.getValues();
				if (currentConfig.equals(panelConfig) == false) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							robotOptionPanel.setValues(currentConfig);
						}
					});
				}
			} catch (InvalidInputException e) {
				Log.debug(e);
			}

		}

	}

}
