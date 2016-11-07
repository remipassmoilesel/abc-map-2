package abcmap.gui.ie.draw;

import java.awt.Component;

import javax.swing.SwingUtilities;

import abcmap.draw.tools.containers.ToolContainer;
import abcmap.events.DrawManagerEvent;
import abcmap.gui.comps.help.ToolHelpPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.stub.MainManager;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;

public class ShowToolHelp extends InteractionElement {

	private ToolHelpPanel toolHelpPanel;

	public ShowToolHelp() {

		label = "Fonctionnement de l'outil actif";
		help = "Cliquez sur cet élément pour visualiser une aide rapide concernant les principales "
				+ "fonctions de l'outil actif.";

		displaySimplyInSearch = false;

		displayInHideableElement = true;

	}

	@Override
	protected Component createPrimaryGUI() {

		// panneau contenant l'aide
		toolHelpPanel = new ToolHelpPanel();
		toolHelpPanel.setMessageNoHelp("<html><i>Aide sur l'outil indisponible.</i></html>");

		// reconstruire le panneau en cas de changement d'outil
		InteractionUpdater interactionUpdater = new InteractionUpdater();
		notifm.setDefaultUpdatableObject(interactionUpdater);

		// ecouter le gestionnaire de dessin
		drawm.getNotificationManager().addObserver(this);

		interactionUpdater.run();

		return toolHelpPanel;
	}

	public class InteractionUpdater implements Runnable, UpdatableByNotificationManager {

		@Override
		public void run() {

			if (toolHelpPanel == null)
				return;

			// recuperer le conteneur de l'outil courant
			ToolContainer currentTC = MainManager.getDrawManager().getCurrentToolContainer();

			// rafraichir le panneau
			toolHelpPanel.constructWith(currentTC);

		}

		/**
		 * Reception d'un evenement
		 */
		@Override
		public void notificationReceived(Notification arg) {
			if (DrawManagerEvent.TOOL_CHANGED.equals(arg.getName())) {
				SwingUtilities.invokeLater(this);
			}
		}

	}

}
