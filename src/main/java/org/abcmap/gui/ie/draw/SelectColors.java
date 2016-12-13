package org.abcmap.gui.ie.draw;

import org.abcmap.core.events.manager.*;
import org.abcmap.gui.components.color.ColorPicker;
import org.abcmap.gui.ie.InteractionElement;

import java.awt.*;

public class SelectColors extends InteractionElement {

    private ColorPicker colorPicker;

    public SelectColors() {

        this.label = "Sélection de couleurs";
        this.help = "Sélectionnez ici les couleurs de premier et de second plan que vous utiliserez "
                + "avec les outils de dessin.";

        this.displaySimplyInSearch = false;

    }

    @Override
    protected Component createPrimaryGUI() {

        colorPicker = new ColorPicker();

//        colorPicker.getListenerHandler().add(new Performer());

        notifm.setDefaultListener(new ColorPickerUpdater());
        drawm.getNotificationManager().addObserver(this);

        return colorPicker;
    }

	/*
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
	*/

    /**
     * @author remipassmoilesel
     */
    private class ColorPickerUpdater implements EventListener {

        @Override
        public void notificationReceived(org.abcmap.core.events.manager.Event arg) {

			/*
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
			*/
        }

    }

}
