package org.abcmap.gui.dialogs.simple;

import org.abcmap.core.utils.Utils;

import javax.swing.*;
import java.io.File;

/**
 * Contains file selection
 */
public class BrowseDialogResult {

    public static final Integer APPROVE = JFileChooser.APPROVE_OPTION;
    public static final Integer CANCEL = JFileChooser.CANCEL_OPTION;

    private Integer returnVal;
    private File file;

    public BrowseDialogResult() {
        returnVal = null;
        file = null;
    }

    public void update(BrowseDialogResult result) {
        this.returnVal = result.returnVal;
        this.file = result.file != null ? new File(result.file.getAbsolutePath()) : null;
    }

    public Integer getReturnVal() {
        return returnVal;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setReturnVal(Integer returnVal) {
        this.returnVal = returnVal;
    }

    @Override
    public String toString() {

        Object[] values = new Object[]{returnVal, file,};
        Object[] keys = new Object[]{"returnVal", "file",};

        return Utils.toString(this, keys, values);

    }

    public boolean isActionCanceled() {
        return Utils.safeEquals(returnVal, CANCEL);
    }

    public boolean isActionApproved() {
        return Utils.safeEquals(returnVal, APPROVE);
    }

}
