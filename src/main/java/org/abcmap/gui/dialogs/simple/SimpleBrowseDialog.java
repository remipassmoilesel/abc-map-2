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

/**
 * This utility regroup several methods to open browse dialogs
 */
public class SimpleBrowseDialog {

    private static final CustomLogger logger = LogManager.getLogger(Project.class);

    /**
     * Only static methods here
     */
    private SimpleBrowseDialog() {
    }

    /**
     * Open a dialog to browse directory
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseDirectory(Window parent) {
        return browseDirectory(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "O.K.");
    }

    /**
     * Open a dialog to browse directory on current thread
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseDirectoryAndWait(final Window parent) {

        GuiUtils.throwIfOnEDT();

        final BrowseDialogResult[] result = new BrowseDialogResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = browseDirectory(parent);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result[0];
    }

    /**
     * Open a dialog to browse path where save project
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProjectToSave(Window parent) {
        return browseFileToSave(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
    }

    /**
     * Open a dialog to browse path where save project
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProjectToSaveAndWait(Window parent) {
        return browseFileToSave(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
    }

    /**
     * Open a dialog to browse project to open
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProjectToOpen(Window parent) {
        return browseFileToOpen(parent, BrowseFileFilter.PROJECTS_FILEFILTER);
    }

    /**
     * Open a dialog to browse project to open on current thread
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProjectToOpenAndWait(final Window parent) {

        GuiUtils.throwIfOnEDT();

        final BrowseDialogResult[] result = new BrowseDialogResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = browseProjectToOpen(parent);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result[0];

    }

    /**
     * Open a dialog to browse a path where save a project
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProfileToSave(Window parent) {
        return browseFileToSave(parent, BrowseFileFilter.PROFILES_FILEFILTER);
    }

    /**
     * Open a dialog to browse a path where save a profile, on current thread
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProfileToSaveAndWait(final Window parent) {

        GuiUtils.throwIfOnEDT();

        final BrowseDialogResult[] result = new BrowseDialogResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = browseProfileToSave(parent);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result[0];

    }

    /**
     * Open a dialog to browse a profile to open
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProfileToOpen(Window parent) {
        return browseFileToOpen(parent, BrowseFileFilter.PROFILES_FILEFILTER);
    }

    /**
     * Open a dialog to browse a profile to open, on current thread
     *
     * @param parent
     * @return
     */
    public static BrowseDialogResult browseProfileToOpenAndWait(final Window parent) {

        GuiUtils.throwIfOnEDT();

        final BrowseDialogResult[] result = new BrowseDialogResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = browseProfileToOpen(parent);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result[0];
    }

    /**
     * Open a dialog to browse a path where save a profile
     *
     * @param parent
     * @return
     */
    private static BrowseDialogResult browseFileToSave(Window parent, BrowseFileFilter filter) {
        return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Enregistrer", filter, true);
    }

    private static BrowseDialogResult browseFileToOpen(Window parent, BrowseFileFilter filter) {
        return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Ouvrir", filter, false);
    }

    public static BrowseDialogResult browseFileToOpen(Window parent) {
        return browseFile(ConfigurationConstants.SYSTEM_HOME_PATH, parent, "Ouvrir", null, false);
    }

    /**
     * Show a browse dialog to select directories
     *
     * @param currentDirectoryPath
     * @param parent
     * @param approveButtonText
     * @return
     */
    private static BrowseDialogResult browseDirectory(String currentDirectoryPath,
                                                      Component parent,
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


    /**
     * Show a browse dialog to select files
     *
     * @param currentDirectoryPath
     * @param parent
     * @param approveButtonText
     * @param filter
     * @param confirmOverwriting
     * @return
     */
    private static BrowseDialogResult browseFile(String currentDirectoryPath,
                                                 Window parent,
                                                 String approveButtonText,
                                                 BrowseFileFilter filter,
                                                 boolean confirmOverwriting) {

        GuiUtils.throwIfNotOnEDT();

        // create a dialog
        JFileChooser fc = new JFileChooser(currentDirectoryPath);
        fc.setDialogTitle("Parcourir");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // add a filter if provided
        if (filter != null) {
            fc.addChoosableFileFilter(filter);
        }

        int returnVal;
        boolean askAgain = false;
        do {

            // display dialog
            returnVal = fc.showDialog(parent, approveButtonText);

            // user answer yes
            if (JFileChooser.APPROVE_OPTION == returnVal) {
                
                // confirm overwriting if needed
                if (confirmOverwriting && fc.getSelectedFile().isFile()) {
                    QuestionResult result = SimpleQuestionDialog.askQuestion(parent, "Le fichier existe déjà, voulez vous l'écraser ?");
                    if (result.isAnswerNo() == true) {
                        askAgain = true;
                    }
                }

            }

        } while (askAgain == true);

        // return result
        BrowseDialogResult bdr = new BrowseDialogResult();
        bdr.setFile(fc.getSelectedFile());
        bdr.setReturnVal(returnVal);

        return bdr;

    }


}
