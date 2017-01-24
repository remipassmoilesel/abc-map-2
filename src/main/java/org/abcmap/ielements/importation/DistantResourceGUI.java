package org.abcmap.ielements.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.resources.DistantResource;
import org.abcmap.core.resources.ShapefileResource;
import org.abcmap.core.resources.WmsResource;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.components.buttons.HtmlLabel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

/**
 * Show a resource in main list
 */
class DistantResourceGUI extends JPanel {

    private DistantResource resource;
    private HtmlCheckbox chkSelected;

    public DistantResourceGUI(DistantResource res) {
        super(new MigLayout("insets 3, gap 3"));

        GuiUtils.throwIfNotOnEDT();

        this.resource = res;

        // name as a checkbox
        chkSelected = new HtmlCheckbox("<b>" + res.getName() + "</b>");
        chkSelected.setToolTipText(res.getName());
        chkSelected.setCursor(GuiCursor.HAND_CURSOR);
        add(chkSelected, "width 98%!, wrap");

        // optional information
        // description as a label
        if (res.getDescription() != null && res.getDescription().isEmpty() == false) {
            HtmlLabel labelDesc = new HtmlLabel(res.getDescription());
            labelDesc.setToolTipText(res.getDescription());
            add(labelDesc, "gapleft 10px!, width 98%!, wrap");
        }

        // shapefile size
        if (res instanceof ShapefileResource) {
            add(new HtmlLabel("Taille: " + ((ShapefileResource) res).getZippedSize() + "mo"), "gapleft 10px!, width 98%!");
        }

        // wms url
        else if (res instanceof WmsResource) {
            WmsResource wmsRes = ((WmsResource) res);
            HtmlLabel labelUrl = new HtmlLabel("URL: " + wmsRes.getUrl());
            labelUrl.setToolTipText(wmsRes.getUrl());
            add(labelUrl, "gapleft 10px!, width 98%!");
        }


    }

    /**
     * Get resource associated with this element
     *
     * @return
     */
    public DistantResource getResource() {
        return resource;
    }

    /**
     * Set this element selected
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        chkSelected.setSelected(selected);
        revalidate();
        repaint();
    }

    /**
     * Return true if this element is selected
     *
     * @return
     */
    public boolean isSelected() {
        return chkSelected.isSelected();
    }

}