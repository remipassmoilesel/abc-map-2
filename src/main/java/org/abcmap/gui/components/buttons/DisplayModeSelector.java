package org.abcmap.gui.components.buttons;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.windows.MainWindowMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DisplayModeSelector extends JPanel {

    private JComboBox<MainWindowMode> combo;

    public DisplayModeSelector() {
        super(new MigLayout("insets 0, gap 3"));

        JLabel lbl = GuiUtils.addLabel("Affichage: ", this, "");
        lbl.setVerticalAlignment(SwingConstants.CENTER);

        combo = new JComboBox<MainWindowMode>(MainWindowMode.values());
        combo.setRenderer(new ListItemRenderer());
        combo.setBorder(null);

        add(combo);

    }

    private class ListItemRenderer extends JLabel implements
            ListCellRenderer<MainWindowMode> {

        @Override
        public Component getListCellRendererComponent(
                JList<? extends MainWindowMode> list, MainWindowMode mode,
                int index, boolean isSelected, boolean cellHasFocus) {

            setText(mode.getLabel());

            if (isSelected || cellHasFocus) {
                setBackground(list.getSelectionBackground());
                setForeground(mode.getFgColor());
            } else {
                setBackground(list.getBackground());
                setForeground(mode.getFgColor());
            }

            revalidate();
            repaint();

            return this;
        }
    }

    public void addActionListener(ActionListener al) {
        combo.addActionListener(al);
    }

    public MainWindowMode getSelectedItem() {
        return (MainWindowMode) combo.getSelectedItem();
    }

    public void setSelectedItem(MainWindowMode mode) {
        combo.setSelectedItem(mode);

        combo.revalidate();
        combo.repaint();
        revalidate();
        repaint();
    }

}
