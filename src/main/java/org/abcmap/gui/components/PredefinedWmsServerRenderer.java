package org.abcmap.gui.components;

import org.abcmap.core.wms.PredefinedWmsServer;

import javax.swing.*;
import java.awt.*;

/**
 * Render WMS servers in JComboBox
 */
public class PredefinedWmsServerRenderer extends JLabel implements ListCellRenderer<PredefinedWmsServer> {

    public PredefinedWmsServerRenderer() {
        super();
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PredefinedWmsServer> list, PredefinedWmsServer server,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        this.setText(server.getName());

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
