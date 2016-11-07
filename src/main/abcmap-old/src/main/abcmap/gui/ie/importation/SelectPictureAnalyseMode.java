package abcmap.gui.ie.importation;

import java.awt.Component;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import abcmap.gui.comps.importation.SurfModePanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;

public class SelectPictureAnalyseMode extends InteractionElement {

	private JSlider slider;

	public SelectPictureAnalyseMode() {
		label = Lng.get("set image analyse");
		help = Lng.get("set image analyse help");

		displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// le slider de selection
		SurfModePanel surfModePanel = new SurfModePanel();
		slider = surfModePanel.getSlider();

		// ecouter le slider
		slider.addChangeListener(new SliderListener());

		// ecouter le gestionnaire d'import et de configuration
		SliderUpdater sliderUpdater = new SliderUpdater();
		notifm.setDefaultUpdatableObject(sliderUpdater);
		configm.getNotificationManager().addObserver(this);

		// mise à jour
		sliderUpdater.run();

		return surfModePanel;

	}

	/**
	 * Met à jour le gestionnaire d'import en fonction du slider
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {

			int value = ((JSlider) e.getSource()).getValue();

			// changer la valeur uniquement si différent
			if (value != configm.getSurfMode()) {

				// affecter la valeur selectionnée
				configm.setSurfMode(value);

			}

		}

	}

	/**
	 * Met à jour le slider en fonction des parametres du gestionnaire d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SliderUpdater implements UpdatableByNotificationManager, Runnable {

		@Override
		public void notificationReceived(Notification arg) {
			SwingUtilities.invokeLater(this);
		}

		@Override
		public void run() {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			if (slider == null)
				return;

			// recuperer la valeur en cours pour comparaison
			int value = configm.getSurfMode();

			if (slider.getValue() != value) {

				// changer sans notification
				GuiUtils.changeWithoutFire(slider, value);
			}
		}
	}

}
