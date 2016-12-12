package org.abcmap.gui.components.layers;

import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Render list of all layers in a JList
 *
 * @author remipassmoilesel
 */
public class LayerListRenderer extends JLabel implements ListCellRenderer<AbstractLayer> {

    private ProjectManager projectm;

    private Color activeLayerColor;

    public LayerListRenderer() {

        GuiUtils.throwIfNotOnEDT();

        this.projectm = MainManager.getProjectManager();

        this.activeLayerColor = new Color(43, 3, 188);

        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends AbstractLayer> list, AbstractLayer layer, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        GuiUtils.throwIfNotOnEDT();

       /* MapLayer activeLayer = null;
        try {
            activeLayer = projectm.getActiveLayer();
        } catch (NullPointerException | MapLayerException e) {
            activeLayer = null;
        }

        // project not initialized
        if (layer == null || activeLayer == null) {
            setText("");
            setIcon(null);
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setBorder(new LineBorder(list.getBackground(), 2, false));
        }

        // project initialized
        else {
            Color cBorder;

            if (activeLayer.equals(layer)) {
                setBackground(activeLayerColor);
                setForeground(Color.white);
                cBorder = Color.LIGHT_GRAY;
            } else {
                setBackground(list.getBackground());
                setForeground(Color.black);
                cBorder = list.getBackground();
            }

            setBorder(new LineBorder(cBorder, 2, false));

            ImageIcon icon;
            if (layer.isVisible()) {
                icon = GuiIcons.LAYER_IS_VISIBLE;
            } else {
                icon = GuiIcons.LAYER_IS_INVISIBLE;
            }
            setIcon(icon);


            setText(layer.getName());
            setFont(list.getFont());

        }

        // cut name of layer if needed
        int i = this.getText().length();
        String txt = this.getText();

        while (this.getPreferredSize().width > list.getSize().width - 15 && i > 0) {
            i -= 2;
            if (i < 1) {
                i = 1;
                this.setText(txt.substring(0, i) + "...");
                this.setSize(getPreferredSize());
                break;
            } else {
                this.setText(txt.substring(0, i) + "...");
                this.setSize(getPreferredSize());
            }
        }
        return this;*/

        return this;
    }
}
