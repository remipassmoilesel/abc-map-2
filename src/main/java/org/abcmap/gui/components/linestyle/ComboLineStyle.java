package org.abcmap.gui.components.linestyle;


import org.abcmap.core.draw.LineStyle;

import javax.swing.*;

/**
 * Line style selection combo
 */
public class ComboLineStyle extends JComboBox<LineStyle> {

    private LineStyleRenderer customRenderer;

    public ComboLineStyle() {
        super(LineStyle.values());

        setEditable(false);

        customRenderer = new LineStyleRenderer();
        setRenderer(customRenderer);
    }

    public static LineStyleRenderer getDefaultRenderer() {
        return new LineStyleRenderer();
    }

}
