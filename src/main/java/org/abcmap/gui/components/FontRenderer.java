package org.abcmap.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Render fonts in JList or combo list
 */
public class FontRenderer extends JLabel implements ListCellRenderer<String> {

    private Dimension defaultDimensions;
    private int defaultFontSize;

    private Font fontToDisplay;
    private String textToDisplay;

    public FontRenderer() {
        super();
        this.setOpaque(true);

        this.defaultFontSize = 13;

        this.defaultDimensions = new Dimension(150, 22);

    }

    public void setDefaultFontSize(int defaultFontSize) {
        this.defaultFontSize = defaultFontSize;
    }

    public void setDefaultDimensions(Dimension defaultDimensions) {
        this.defaultDimensions = defaultDimensions;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String f, int arg2, boolean isSelected,
                                                  boolean arg4) {

        if (f != null) {
            this.fontToDisplay = new Font(f, Font.PLAIN, defaultFontSize);
            this.textToDisplay = "  " + f;
        } else {
            this.fontToDisplay = new Font(Font.DIALOG, Font.PLAIN, defaultFontSize);
            this.textToDisplay = "  " + "-- Police indisponible";
        }

        this.setFont(fontToDisplay);
        this.setText(textToDisplay);

        this.setPreferredSize(defaultDimensions);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

}
