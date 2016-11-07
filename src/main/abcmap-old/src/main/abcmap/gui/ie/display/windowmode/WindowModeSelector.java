package abcmap.gui.ie.display.windowmode;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import abcmap.events.GuiManagerEvent;
import abcmap.gui.comps.buttons.DisplayModeSelector;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.windows.MainWindowMode;
import abcmap.utils.Utils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;

public class WindowModeSelector extends InteractionElement {

	private DisplayModeSelector selector;

	public WindowModeSelector() {

		label = "Sélection du mode d'affichage";
		help = "Sélectionnez ici le mode d'affichage de la fenêtre.";

	}

	@Override
	public Component createPrimaryGUI() {

		// créer l'element
		selector = new DisplayModeSelector();

		// ecouter les changements
		selector.addActionListener(new ComboWindowModeListener());

		/*
		 * Mettre à jour le sélecteur en fonction des changements de mode
		 */
		notifm.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(Notification arg) {
				if (GuiManagerEvent.isWindowModeNotification(arg)) {
					selector.setSelectedItem(guim.getMainWindowMode());
				}
			}
		});

		guim.getNotificationManager().addObserver(this);

		return selector;
	}

	/**
	 * Changer le mode de fenetre en fonction de la saisie de l'utilisateur
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ComboWindowModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// récupérer le mode sélectionné
			JComboBox<MainWindowMode> src = (JComboBox<MainWindowMode>) e
					.getSource();
			MainWindowMode selectedMode = (abcmap.gui.windows.MainWindowMode) src
					.getSelectedItem();

			if (Utils.safeEquals(guim.getMainWindowMode(), selectedMode) == false) {
				guim.setMainWindowMode(selectedMode);
			}

		}

	}

}
