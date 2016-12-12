package org.abcmap.gui.toolbars;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.CustomComponent;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolbarButton extends CustomComponent {

    private InteractionElement interactionElement;

    public ToolbarButton(InteractionElement ie) {

        setLayout(new MigLayout("insets 3"));

        interactionElement = ie;

        // icones
        JLabel icon = new JLabel(
                interactionElement.getMenuIcon() != null ? interactionElement
                        .getMenuIcon() : GuiIcons.DEFAULT_TOOLBAR_BUTTON_ICON);
        icon.setOpaque(false);
        add(icon, "dock center");

        setToolTipText(ie.getLabel());

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ThreadManager.runLater(interactionElement);
            }
        });
    }

}
