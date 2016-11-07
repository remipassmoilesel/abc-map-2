package abcmap.gui.ie.draw;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.draw.tools.containers.ToolContainer;
import abcmap.events.DrawManagerEvent;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;

public class ShowToolOptionPanel extends InteractionElement {

	private JPanel toolOptionsPanel;
	private SimpleBlockItem simpleMenuItem;

	public ShowToolOptionPanel() {

		label = "Options de l'outil";
		help = "Cliquez ici pour afficher les options de l'outil actif.";

		displaySimplyInSearch = false;

	}

	@Override
	protected Component createBlockGUI() {

		// creer un element d'IHM
		simpleMenuItem = SimpleBlockItem.create(this, toolOptionsPanel);

		// se mettre a jour aux changements d'outils
		UpdateToolOptionPanel optionPanelUpdater = new UpdateToolOptionPanel();
		notifm.setDefaultUpdatableObject(optionPanelUpdater);
		drawm.getNotificationManager().addObserver(this);

		optionPanelUpdater.run();

		return simpleMenuItem;

	}

	public class UpdateToolOptionPanel implements
			UpdatableByNotificationManager, Runnable {

		/**
		 * Reception d'un evenement
		 */
		@Override
		public void notificationReceived(Notification arg) {
			if (arg instanceof DrawManagerEvent)
				SwingUtilities.invokeLater(this);
		}

		@Override
		public void run() {

			// pas d'actions hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			if (simpleMenuItem == null)
				return;

			// recuperer le container d'outils
			ToolContainer currentTC = drawm.getCurrentToolContainer();

			// aucun outil sélectionné, création d'un panneau vide
			if (currentTC == null) {
				toolOptionsPanel = new JPanel(new MigLayout());
				toolOptionsPanel.add(new HtmlLabel(
						"<html><i>Veuillez sélectionner un outil.</i></html>"));
			}

			// pas d'options disponibles: création d'un panneau vide
			else if (currentTC.getOptionPanel() == null) {
				toolOptionsPanel = new JPanel(new MigLayout());
				toolOptionsPanel.add(new HtmlLabel(
						"<html><i>Aucune option n'est disponible.</i></html>"));
			}

			// un panneau d'options est disponible
			else {
				toolOptionsPanel = currentTC.getOptionPanel();
			}

			// affecter au composants
			simpleMenuItem.setBottomComponent(toolOptionsPanel);

			// recontruire en affichant le panneau d'options
			simpleMenuItem.reconstruct();

		}

	}

}
