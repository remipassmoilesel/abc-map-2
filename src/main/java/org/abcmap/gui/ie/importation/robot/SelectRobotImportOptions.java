package org.abcmap.gui.ie.importation.robot;

import org.abcmap.core.events.manager.*;
import org.abcmap.gui.components.importation.RobotImportOptionsPanel;
import org.abcmap.gui.ie.InteractionElement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectRobotImportOptions extends InteractionElement {

    private RobotImportOptionsPanel robotOptionPanel;

    public SelectRobotImportOptions() {
        label = "Options de capture automatique";
        help = "...";
    }

    @Override
    protected Component createPrimaryGUI() {

        robotOptionPanel = new RobotImportOptionsPanel();


        robotOptionPanel.getListenerHandler().add(new RobotConfigurationUpdater());

        configm.getNotificationManager().addObserver(this);
        notifm.setDefaultListener(new PanelUpdater());

        return robotOptionPanel;

    }

    private class RobotConfigurationUpdater implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            /*
            try {
                RobotConfiguration panelValues = robotOptionPanel.getValues();
                if (configm.getRobotConfiguration().equals(panelValues) == false) {
                    configm.setRobotConfiguration(panelValues);
                }
            } catch (InvalidInputException e1) {
                // Log.debug(e1);
            }
            */
        }

    }

    private class PanelUpdater implements EventListener {

        @Override
        public void notificationReceived(org.abcmap.core.events.manager.Event arg) {

            /*
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

            */
        }

    }

}
