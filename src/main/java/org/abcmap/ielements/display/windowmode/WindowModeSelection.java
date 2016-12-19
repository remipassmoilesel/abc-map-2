package org.abcmap.ielements.display.windowmode;

import org.abcmap.core.events.GuiManagerEvent;
import org.abcmap.core.events.manager.EventListener;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.buttons.DisplayModeSelector;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.windows.MainWindow;
import org.abcmap.gui.windows.MainWindowMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowModeSelection extends InteractionElement {

    private DisplayModeSelector selector;

    public WindowModeSelection() {
        label = "Sélection du mode d'affichage";
        help = "Sélectionnez ici le mode d'affichage de la fenêtre.";
    }

    @Override
    public Component createPrimaryGUI() {

        selector = new DisplayModeSelector();

        selector.addActionListener(new ComboWindowModeListener());

        notifm.setDefaultListener(new EventListener() {
            @Override
            public void notificationReceived(org.abcmap.core.events.manager.Event arg) {

                if (GuiManagerEvent.isWindowModeNotification(arg)) {

                    selector.setSelectedItem(guim.getMainWindow().getWindowMode());
                }
            }
        });

        guim.getNotificationManager().addObserver(this);

        return selector;
    }

    /**
     * Change window mode on user selection
     *
     * @author remipassmoilesel
     */
    private class ComboWindowModeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            MainWindow mainWindow = guim.getMainWindow();

            if (mainWindow == null) {
                return;
            }

            JComboBox<MainWindowMode> src = (JComboBox<MainWindowMode>) e.getSource();
            MainWindowMode selectedMode = (MainWindowMode) src.getSelectedItem();

            if (Utils.safeEquals(mainWindow.getWindowMode(), selectedMode) == false) {
                mainWindow.setWindowMode(selectedMode);
            }

        }

    }

}
