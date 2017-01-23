package org.abcmap.gui.components;

import org.abcmap.core.resources.WmsResource;

import javax.swing.*;
import java.awt.*;

/**
 * Render WMS servers in JComboBox
 */
public class PredefinedWmsServerRenderer extends JLabel implements ListCellRenderer<WmsResource> {

    public PredefinedWmsServerRenderer() {
        super();
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends WmsResource> list, WmsResource server,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        // show server name
        if (server == null) {
            this.setText("Name is null");
        } else {
            this.setText(server.getName());
        }

        // change color if selected
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
