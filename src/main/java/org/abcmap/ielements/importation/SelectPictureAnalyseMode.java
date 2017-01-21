package org.abcmap.ielements.importation;

import org.abcmap.core.events.manager.EventListener;
import org.abcmap.gui.components.importation.SurfModePanel;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Select picture analyse mode
 */
public class SelectPictureAnalyseMode extends InteractionElement {

    /**
     * SURF mode selection slider
     */
    private JSlider slider;

    public SelectPictureAnalyseMode() {
        label = "Mode d'analyse d'image";
        help = "...";

        displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        // create GUI
        SurfModePanel surfModePanel = new SurfModePanel();
        slider = surfModePanel.getSlider();

        // listen user input
        slider.addChangeListener(new SliderListener());

        // listen configuration change
        SliderUpdater sliderUpdater = new SliderUpdater();
        notifm.addEventListener(sliderUpdater);
        configm().getNotificationManager().addObserver(this);

        // first update
        sliderUpdater.run();

        return surfModePanel;

    }

    /**
     * Listen user input and change configuration
     */
    private class SliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {

            // get value
            int value = slider.getValue();

            // change if needed
            if (value != configm().getSurfMode()) {
                configm().setSurfMode(value);
            }

        }

    }

    /**
     *
     */
    private class SliderUpdater extends FormUpdater{

        @Override
        protected void updateFormFields() {
            super.updateFormFields();

            int value = configm().getSurfMode();

            if (slider.getValue() != value) {
                GuiUtils.changeWithoutFire(slider, value);
            }
        }
    }

}
