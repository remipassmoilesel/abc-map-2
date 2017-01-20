package org.abcmap.gui.dialogs.simple;

import org.abcmap.core.utils.Utils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

/**
 * Result returned by a browse dialog. Contain file or directory selected by user if any.
 */
public class BrowseDialogResult {

    public static final Integer APPROVE = JFileChooser.APPROVE_OPTION;
    public static final Integer CANCEL = JFileChooser.CANCEL_OPTION;

    /**
     * Value returned by browse dialog
     */
    private Integer returnVal;

    /**
     * File selected, if any
     */
    private File file;

    public BrowseDialogResult() {
        returnVal = null;
        file = null;
    }

    public void update(BrowseDialogResult result) {
        this.returnVal = result.returnVal;
        this.file = result.file != null ? new File(result.file.getAbsolutePath()) : null;
    }

    /**
     * Return value returned by browse dialog. Equal to BrowseDialogResult.APPROVE or CANCEL.
     *
     * @return
     */
    public Integer getReturnVal() {
        return returnVal;
    }

    /**
     * Return selected file, if any
     *
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * Return selected file as a Path object, if any
     *
     * @return
     */
    public Path getPath() {
        return file != null ? file.toPath() : null;
    }

    /**
     * Set selected file
     *
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Set return value of dialog
     *
     * @param returnVal
     */
    public void setReturnVal(Integer returnVal) {
        this.returnVal = returnVal;
    }

    @Override
    public String toString() {
        return "BrowseDialogResult{" +
                "returnVal=" + returnVal +
                ", file=" + file +
                '}';
    }

    /**
     * Return true if action was canceled, false otherwise
     *
     * @return
     */
    public boolean isActionCanceled() {
        return Utils.safeEquals(returnVal, CANCEL);
    }

    /**
     * Return true if action was approved, false otherwise
     *
     * @return
     */
    public boolean isActionApproved() {
        return Utils.safeEquals(returnVal, APPROVE);
    }

}
