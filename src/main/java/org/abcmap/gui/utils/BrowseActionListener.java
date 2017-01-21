package org.abcmap.gui.utils;

import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.gui.dialogs.simple.SimpleBrowseDialog;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility which can be associated with a button to open a browse dialog.
 * <p>
 * After user selection it update a specified text field
 */
public class BrowseActionListener implements ActionListener {

    private final Runnable toRunOnSelection;

    public enum Type {
        FILES_ONLY,
        DIRECTORY_ONLY,
    }

    /**
     * Component to update
     */
    private JTextComponent componentToUpdate;

    /**
     * Specify if this dialog can select files or directories
     */
    private Type type;

    /**
     * @param componentToUpdate
     * @param type
     */
    public BrowseActionListener(JTextComponent componentToUpdate, Type type, Runnable toRunOnSelection) {

        this.type = type;
        this.toRunOnSelection = toRunOnSelection;
        this.componentToUpdate = componentToUpdate;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // get window parent
        Window parent = SwingUtilities.windowForComponent((Component) e.getSource());

        // show browse dialog
        BrowseDialogResult bdr;
        if (Type.DIRECTORY_ONLY.equals(type)) {
            bdr = SimpleBrowseDialog.browseDirectory(parent);
        } else {
            bdr = SimpleBrowseDialog.browseFileToOpen(parent);
        }

        // check if user did not cancel action
        if (bdr.isActionCanceled() == false) {

            // change value later
            SwingUtilities.invokeLater(() -> {
                String path = bdr.getFile().getAbsolutePath();
                GuiUtils.changeTextWithoutFire(componentToUpdate, path);

                if (toRunOnSelection != null) {
                    ThreadManager.runLater(toRunOnSelection);
                }
            });
        }
    }
}
