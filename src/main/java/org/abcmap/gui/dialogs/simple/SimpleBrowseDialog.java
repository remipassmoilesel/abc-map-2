package org.abcmap.gui.dialogs.simple;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class SimpleBrowseDialog {

    private static final CustomLogger logger = LogManager.getLogger(Project.class);

    public static BrowseDialogResult browseDirectory(Window parent) {
        return browseDirectory(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "O.K.");
    }

    public static BrowseDialogResult browseDirectoryAndWait(final Window parent) {

        final BrowseDialogResult result = new BrowseDialogResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(browseDirectory(parent));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;
    }

    public static BrowseDialogResult browseProjectToSave(Window parent) {
        return browseFileToSave(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
    }

    public static BrowseDialogResult browseProjectToOpen(Window parent) {
        return browseFileToOpen(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
    }

    public static BrowseDialogResult browseProjectToOpenAndWait(final Window parent) {

        final BrowseDialogResult result = new BrowseDialogResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(browseProjectToOpen(parent));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;

    }

    public static BrowseDialogResult browseProfileToSave(Window parent) {
        return browseFileToSave(parent, BrowseFileFilter.PROFILES_FILEFILTER);
    }

    public static BrowseDialogResult browseProfileToSaveAndWait(final Window parent) {

        final BrowseDialogResult result = new BrowseDialogResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(browseProfileToSave(parent));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;

    }

    public static BrowseDialogResult browseProfileToOpen(Window parent) {
        return browseFileToOpen(parent, BrowseFileFilter.PROFILES_FILEFILTER);
    }

    public static BrowseDialogResult browseProfileToOpenAndWait(final Window parent) {

        final BrowseDialogResult result = new BrowseDialogResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(browseProfileToOpen(parent));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;
    }

    public static BrowseDialogResult browseFileToSave(Window parent, BrowseFileFilter filter) {
        return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Enregistrer", filter,
                true);
    }

    public static BrowseDialogResult browseFileToSaveAndWait(final Window parent,
                                                             final BrowseFileFilter filter) {

        final BrowseDialogResult result = new BrowseDialogResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(browseFileToSave(parent, filter));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;
    }

    public static BrowseDialogResult browseFileToOpen(Window parent, BrowseFileFilter filter) {
        return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Ouvrir", filter, false);
    }

    public static BrowseDialogResult browseFileToOpenAndWait(final Window parent,
                                                             final BrowseFileFilter filter) {

        final BrowseDialogResult result = new BrowseDialogResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent,
                            "Ouvrir", filter, false));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;

    }

    public static BrowseDialogResult browseDirectory(String currentDirectoryPath, Component parent,
                                                     String approveButtonText) {

        GuiUtils.throwIfNotOnEDT();

        JFileChooser fc = new JFileChooser(currentDirectoryPath);
        fc.setDialogTitle("Parcourir");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = fc.showDialog(parent, approveButtonText);

        BrowseDialogResult bdr = new BrowseDialogResult();
        bdr.setFile(fc.getSelectedFile());
        bdr.setReturnVal(returnVal);

        return bdr;

    }

    public static BrowseDialogResult browseFile(String currentDirectoryPath, Window parent,
                                                String approveButtonText, BrowseFileFilter filter, boolean confirmOverwriting) {

        GuiUtils.throwIfNotOnEDT();

        // preparation de la boite de dialogue
        JFileChooser fc = new JFileChooser(currentDirectoryPath);
        fc.setDialogTitle("Parcourir");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // filter
        if (filter != null) {
            fc.addChoosableFileFilter(filter);
        }

        int returnVal;
        boolean askAgain;
        do {

            askAgain = true;

            // affichage du dialog
            returnVal = fc.showDialog(parent, approveButtonText);

            // check if file exist
            if (confirmOverwriting && fc.getSelectedFile() != null && fc.getSelectedFile().isFile()) {
                QuestionResult result = SimpleQuestionDialog.askQuestion(parent, "Ecraser le fichier ?");
                if (result.getReturnVal().equals(QuestionResult.YES)) {
                    askAgain = false;
                }
            } else {
                askAgain = false;
            }

        } while (askAgain);

        // return result
        BrowseDialogResult bdr = new BrowseDialogResult();
        bdr.setFile(fc.getSelectedFile());
        bdr.setReturnVal(returnVal);

        return bdr;

    }

}
