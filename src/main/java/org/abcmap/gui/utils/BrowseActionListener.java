package org.abcmap.gui.utils;

import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.gui.dialogs.simple.SimpleFileFilter;
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

    public enum Type {
        /**
         * Browse dialog will show only files
         */
        FILES_ONLY,

        /**
         * Browse dialog will show only directories
         */
        DIRECTORY_ONLY,
    }


    /**
     * Runnable called when one file or directory is selected
     */
    private final Runnable toRunOnSelection;

    /**
     * File filter used to display files or not
     */
    private final SimpleFileFilter fileFilter;


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
    public BrowseActionListener(Type type, JTextComponent componentToUpdate, SimpleFileFilter filter, Runnable toRunOnSelection) {

        this.type = type;
        this.fileFilter = filter;
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
            bdr = SimpleBrowseDialog.browseFileToOpen(parent, fileFilter);
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
