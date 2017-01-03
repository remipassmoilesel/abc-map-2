package org.abcmap.ielements.importation;

import org.abcmap.core.events.manager.*;
import org.abcmap.gui.components.importation.SurfModePanel;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SelectPictureAnalyseMode extends InteractionElement {

    private JSlider slider;

    public SelectPictureAnalyseMode() {
        label = "GoToWebsiteMode d'analyse d'image";
        help = "...";

        displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        SurfModePanel surfModePanel = new SurfModePanel();
        slider = surfModePanel.getSlider();

        slider.addChangeListener(new SliderListener());

        SliderUpdater sliderUpdater = new SliderUpdater();
        notifm.setDefaultListener(sliderUpdater);
        configm().getNotificationManager().addObserver(this);

        sliderUpdater.run();

        return surfModePanel;

    }

    private class SliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {

            int value = ((JSlider) e.getSource()).getValue();

            if (value != configm().getSurfMode()) {
                configm().setSurfMode(value);
            }

        }

    }

    private class SliderUpdater implements EventListener, Runnable {

        @Override
        public void notificationReceived(org.abcmap.core.events.manager.Event arg) {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void run() {

            GuiUtils.throwIfNotOnEDT();

            if (slider == null) {
                return;
            }

            int value = configm().getSurfMode();

            if (slider.getValue() != value) {

                GuiUtils.changeWithoutFire(slider, value);
            }
        }
    }

}
