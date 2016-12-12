package org.abcmap.gui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Allow user to activate tools
 */
public class ToolSelectionPanel extends JPanel {

    private static final Integer TOOLS_PER_LINE = 3;
    private ArrayList<JToggleButton> buttons;

    public ToolSelectionPanel() {
        super();


        setLayout(new MigLayout("gap 5, insets 5"));

        /**
        ToolContainer[] tools = ToolLibrary.getAvailablesTools();

        ButtonGroup bg = new ButtonGroup();
        buttons = new ArrayList<>();

        // initialized at 1 for columns
        int i = 1;

        for (ToolContainer tc : tools) {

            ImageIcon icon = tc.getIcon();
            String tip = tc.getReadableName();

            JToggleButton bt = new JToggleButton(icon);
            bt.setToolTipText(tip);
            bt.setActionCommand(tc.getId());

            bg.add(bt);
            buttons.add(bt);

            String csts = i % TOOLS_PER_LINE == 0 ? "wrap," : "";
            this.add(bt, csts + "width 50px!");

            i++;
        }
         */
    }

    public void addActionListener(ActionListener listener) {
//        for (JToggleButton btn : buttons) {
//            btn.addActionListener(listener);
//        }
    }

}
