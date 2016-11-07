package abcmap.gui.ie.draw;

import java.awt.Component;

import abcmap.draw.DrawConstants;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.events.DrawManagerEvent;
import abcmap.gui.comps.color.ColorPicker;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.stub.MainManager;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.utils.ShapeUpdater;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;

/**
 * Element d'interaction de choix de couleur. Cette objet sert d'interface entre
 * le gestionnaire de dessin et le panneau de selection de couleur.
 * 
 * @author remipassmoilesel
 *
 */
public class SelectColors extends InteractionElement {

	private ColorPicker colorPicker;

	public SelectColors() {

		// caracteristiques
		this.label = "Sélection de couleurs";
		this.help = "Sélectionnez ici les couleurs de premier et de second plan que vous utiliserez "
				+ "avec les outils de dessin.";

		// affichage complexe dans la recherche
		this.displaySimplyInSearch = false;

	}

	@Override
	protected Component createPrimaryGUI() {

		// selecteur de couleur
		colorPicker = new ColorPicker();

		// mettre a l'ecoute l'element d'interaction
		colorPicker.getListenerHandler().add(new Performer());

		// ecouter les changements en provenance du gestionnaire d'evenement
		notifm.setDefaultUpdatableObject(new ColorPickerUpdater());
		drawm.getNotificationManager().addObserver(this);

		return colorPicker;
	}

	private class Performer extends ShapeUpdater {

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			// adapter le mode de modification en fonction du colorpicker
			setMode(colorPicker.isForegroundActive() ? DrawConstants.MODIFY_FG_COLOR
					: DrawConstants.MODIFY_BG_COLOR);

			// recuperer les caracteristiques du trait
			DrawProperties st = drawm.getNewStroke();
			DrawPropertiesContainer pp = (DrawPropertiesContainer) st.getProperties();

			pp.fgColor = colorPicker.getSelectedFgColor();
			pp.bgColor = colorPicker.getSelectedBgColor();

			setProperties(pp);

			// mettre à jour le manager de dessin
			drawm.setStroke(st);

		}
	}

	/**
	 * Updater particulier car composant exotique
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ColorPickerUpdater implements UpdatableByNotificationManager {

		@Override
		public void notificationReceived(Notification arg) {

			// filtrer les evenements
			if (arg instanceof DrawManagerEvent) {
				if (DrawManagerEvent.DRAW_STROKE_CHANGED.equals(arg.getName())) {

					// recuperer les couleurs
					DrawProperties st = MainManager.getDrawManager().getNewStroke();

					// mettre a jour le panneau de selection de couleur
					colorPicker.setFgColor(st.getFgColor(), false);
					colorPicker.setBgColor(st.getBgColor(), false);

					colorPicker.repaint();

				}
			}
		}

	}

}
