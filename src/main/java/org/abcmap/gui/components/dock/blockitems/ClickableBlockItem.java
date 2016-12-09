package org.abcmap.gui.components.dock.blockitems;

import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickableBlockItem extends SimpleBlockItem {

    private static final String CLICKABLE_MENU_ITEM_ACTION = "CLICKABLE_MENU_ITEM_ACTION";

    public static ClickableBlockItem create(InteractionElement elmt) {
        ClickableBlockItem smi = new ClickableBlockItem(elmt);
        smi.reconstruct();
        return smi;
    }

    private AbstractAction fireAction;

    public ClickableBlockItem(InteractionElement elmt) {
        super(elmt);

        this.changeColorUnderFocus(true);
        this.setOpaque(true);
        this.labelName.setCursor(GuiCursor.HAND_CURSOR);

        this.fireAction = new FireAction();
        setFocusable(true);

        // run action when user use space bar
        this.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), CLICKABLE_MENU_ITEM_ACTION);
        this.getActionMap().put(CLICKABLE_MENU_ITEM_ACTION, fireAction);

        this.labelName.addMouseListener(new ItemActionListener());
    }

    private class FireAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            ThreadManager.runLater(interactionElmt);
        }
    }

    private class ItemActionListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            fireAction.actionPerformed(new ActionEvent(ClickableBlockItem.this,
                    ActionEvent.ACTION_PERFORMED, CLICKABLE_MENU_ITEM_ACTION));
        }

    }

}
