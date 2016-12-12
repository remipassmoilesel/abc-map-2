package org.abcmap.gui.dialogs.simple;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper to display simple dialogs
 */
public class SimpleDialogs {

    private static final CustomLogger logger = LogManager.getLogger(SimpleDialogs.class);

    public static void showInformationAndWait(final Window parent, String message) {
        showInformationAndWait(parent, "Information", message);
    }

    public static void showInformationAndWait(final Window parent, final String title, final String message) {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    SimpleInformationDialog sd = new SimpleInformationDialog(parent);
                    sd.setTitle(title);
                    sd.setMessage(message);
                    sd.reconstruct();

                    sd.setVisible(true);
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
        }

    }

    public static void showInformationLater(final Window parent, final String title, final String message) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleInformationDialog sd = new SimpleInformationDialog(parent);
                sd.setTitle(title);
                sd.setMessage(message);
                sd.reconstruct();

                sd.setVisible(true);
            }
        });
    }

    public static void showInformationLater(Window parent, String message) {
        showInformationLater(parent, "Information", message);
    }

}
