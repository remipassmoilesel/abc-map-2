package org.abcmap.gui.components.layers;

import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Render list of layers in a JList
 *
 * @author remipassmoilesel
 */
public class LayerListRenderer extends JLabel implements ListCellRenderer<AbstractLayer> {


    private ProjectManager projectm;

    /**
     * Color used to draw active layer background when list is disabled
     */
    private Color defaultActiveLayerColorWhenListDisabled;

    /**
     * Color used to draw active layer background when list is enabled
     */
    private Color defaultActiveLayerColor;

    public LayerListRenderer() {

        GuiUtils.throwIfNotOnEDT();

        this.projectm = Main.getProjectManager();

        this.defaultActiveLayerColor = new Color(43, 3, 188);
        this.defaultActiveLayerColorWhenListDisabled = new Color(155, 168, 188);

        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends AbstractLayer> list, AbstractLayer layer, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        GuiUtils.throwIfNotOnEDT();

        Project project = projectm.getProject();
        AbstractLayer activeLayer = project.getActiveLayer();

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
            Color activeColor = list.isEnabled() ? defaultActiveLayerColor : defaultActiveLayerColorWhenListDisabled;

            if (activeLayer.equals(layer)) {
                setBackground(activeColor);
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
        return this;
    }
}
