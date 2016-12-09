package org.abcmap.gui.components.dock.blockitems;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.HtmlLabel;

import javax.swing.*;

public class BlockItemHelpArea extends JPanel {

    private HtmlLabel labelHelp;

    public BlockItemHelpArea() {
        super(new MigLayout("insets 5"));
        GuiStyle.applyStyleTo(GuiStyle.SIMPLE_BLOCK_ITEM_HELP, this);

        labelHelp = new HtmlLabel("No help");
        labelHelp.setStyle(GuiStyle.SIMPLE_BLOCK_ITEM_HELP);

        add(labelHelp, "grow");
    }

    public BlockItemHelpArea(String help) {
        this();
        setText(help);
    }

    public void setText(String help) {
        labelHelp.setText(help);
    }

    public void refresh() {
        revalidate();
        repaint();
    }

}
