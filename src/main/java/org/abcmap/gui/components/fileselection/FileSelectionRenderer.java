package org.abcmap.gui.components.fileselection;

import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;


/**
 * Render files in a JList
 */
public class FileSelectionRenderer extends JLabel implements ListCellRenderer<File> {

    /**
     * Color used when file exist
     */
    private Color fileExistColor;

    /**
     * Color used when file not exist
     */
    private Color fileNotExistColor;

    public FileSelectionRenderer() {

        GuiUtils.throwIfNotOnEDT();

        this.fileNotExistColor = new Color(55, 0, 0);
        this.fileExistColor = new Color(0, 0, 200);

        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends File> list, File file, int index, boolean isSelected,
                                                  boolean cellHasFocus) {

        GuiUtils.throwIfNotOnEDT();

        // set text of label
        setText(file.getName());

        // change foreground color relative to file existence
        // file exist
        if (file.isFile()) {
            setForeground(fileExistColor);
            setToolTipText(file.getAbsolutePath());
        }
        // file does not exist
        else {
            setForeground(fileNotExistColor);
            setToolTipText("Ce fichier n'existe pas: " + file.getAbsolutePath());
        }

        // apply background colors relative to selection
        if (isSelected) {
            setBackground(list.getSelectionBackground());
        } else {
            setBackground(list.getBackground());
        }

        return this;

    }

}
