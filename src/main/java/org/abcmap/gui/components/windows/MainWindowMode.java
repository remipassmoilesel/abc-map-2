package org.abcmap.gui.components.windows;

import java.awt.Color;

/**
 * Main window is displayable in several mode. Each mode have its own color and label.
 */
public enum MainWindowMode {

    SHOW_MAP(Color.green, "Carte"),

    SHOW_LAYOUTS(Color.blue, "Feuilles de mise en page"),

    SHOW_REFUSED_TILES(Color.red, "Tuiles refusées");

    private String label;
    private Color fgColor;

    private MainWindowMode(Color fgColor, String label) {

        this.label = label;
        this.fgColor = fgColor;

    }

    public String getLabel() {
        return label;
    }

    public Color getFgColor() {
        return fgColor;
    }

}
