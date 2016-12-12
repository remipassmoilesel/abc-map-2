package org.abcmap.gui.toolbars;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class Toolbar extends JPanel {

    public Toolbar() {
        super(new MigLayout("insets 2, gap 2"));
        setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
    }

    public void addInteractionElement(InteractionElement element) {
        add(new ToolbarButton(element), "width 35!, height 35!");
    }

}
