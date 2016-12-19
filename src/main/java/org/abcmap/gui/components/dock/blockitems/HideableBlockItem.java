package org.abcmap.gui.components.dock.blockitems;

import org.abcmap.gui.GuiCursor;
import org.abcmap.ielements.InteractionElement;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HideableBlockItem extends SimpleBlockItem {

    public static HideableBlockItem create(InteractionElement ie, Component bottom, boolean bottomIsVisible) {
        HideableBlockItem smi = new HideableBlockItem(ie);
        smi.setBottomComponent(bottom);
        smi.setBottomComponentVisible(bottomIsVisible);
        smi.reconstruct();
        return smi;
    }

    // private JLabel hideButton;
    private boolean bottomElementVisible = true;
    private HideActionListener hideActionListener;

    public HideableBlockItem(InteractionElement elmt) {
        super(elmt);

        changeColorUnderFocus(true);

        this.hideActionListener = new HideActionListener();
        labelName.addMouseListener(hideActionListener);

        labelName.setCursor(GuiCursor.HAND_CURSOR);

        reconstruct(helpVisible);

    }

    @Override
    public void reconstruct(boolean showHelp) {
        super.reconstruct(showHelp);

        if (bottomElementVisible == false) {
            if (bottomComponent != null)
                remove(bottomComponent);
        }

    }

    @Override
    public void reconstruct() {
        setBottomComponentVisible(bottomElementVisible);
        reconstruct(helpVisible);
    }

    public void refresh(boolean showHelp, boolean showBottomElement) {
        setBottomComponentVisible(showBottomElement);
        reconstruct(showHelp);
    }

    private class HideActionListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            bottomElementVisible = !bottomElementVisible;
            reconstruct(helpVisible);
        }

    }

    public void setBottomComponentVisible(boolean bottomElementVisible) {
        this.bottomElementVisible = bottomElementVisible;
    }

}
