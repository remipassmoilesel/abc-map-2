package org.abcmap.gui.components.fileselection;

import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.gui.GuiColors;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;


/**
 * Render files in a JList
 */
public class FileSelectionRenderer extends JLabel implements ListCellRenderer<File> {

    private ProjectManager projectm;
    private GuiManager guim;

    private Color fileExistColor;
    private Color fileNotExistColor;
    private Color selectedColor;

    public FileSelectionRenderer() {

        GuiUtils.throwIfNotOnEDT();

        this.projectm = Main.getProjectManager();
        this.guim = Main.getGuiManager();

        this.fileNotExistColor = new Color(55, 0, 0);
        this.fileExistColor = new Color(0, 0, 200);
        this.selectedColor = GuiColors.FOCUS_COLOR_BACKGROUND.brighter().brighter();

        // caracteristiques du label
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends File> list, File file, int index, boolean isSelected,
                                                  boolean cellHasFocus) {

        GuiUtils.throwIfNotOnEDT();

        setText(file.getName());

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

        if (file.equals(list.getSelectedValue())) {
            setBackground(selectedColor);
        } else {
            setBackground(list.getBackground());
        }

        return this;

    }

}
