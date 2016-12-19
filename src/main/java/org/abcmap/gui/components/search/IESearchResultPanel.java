package org.abcmap.gui.components.search;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.CustomComponent;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.components.dock.blockitems.HideableBlockItem;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Component which allow to show results
 */
public class IESearchResultPanel extends JPanel {

    private static final String SIMPLE = "SIMPLE";
    private static final String SUB_BOX = "SUB_BOX";

    private InteractionElement ielement;
    private InteractivePopupDisplay popupParent;

    private String displayMode;
    private int maxWidth;
    private int interactionGuiWidth;

    public IESearchResultPanel(InteractivePopupDisplay popupParent, InteractionElement ie) {

        GuiUtils.throwIfNotOnEDT();

        this.ielement = ie;
        this.popupParent = popupParent;

        this.maxWidth = CommandSearchTextField.POPUP_WIDTH_PX - 20;
        this.interactionGuiWidth = 300;

        this.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
        this.setLayout(new MigLayout("insets 5"));


        displayAsSimpleSearchResult();

    }

    /**
     * Display element as a simple clickable search result
     */
    private void displayAsSimpleSearchResult() {

        GuiUtils.throwIfNotOnEDT();

        displayMode = SIMPLE;
        removeAll();

        // command name and icon
        CustomComponent comp = new CustomComponent();
        comp.addActionListener(new CustomActionListener());

        if (ielement.getMenuIcon() != null) {
            comp.add(new JLabel(ielement.getMenuIcon()), "width 25px!");
        }

        GuiUtils.addLabel(ielement.getLabel(), comp, null, GuiStyle.SEARCH_RESULT_LABEL);

        this.add(comp, "width " + maxWidth + "px!, wrap");

        // show keyboard shortcut
        if (ielement.getAccelerator() != null) {

            String str = "Raccourci: " + Utils.keystrokeToString(ielement.getAccelerator());

            GuiUtils.addLabel(str, this, "width max, wrap", GuiStyle.SEARCH_RESULT_TEXT);

        }

        // show help
        if (ielement.getHelp() != null) {
            GuiUtils.addLabel("Aide: " + ielement.getHelp(), this, "gapx 10, span, width max, wrap", GuiStyle.SEARCH_RESULT_TEXT);
        }

        // show message if command is not available by search
        if (ielement.getNoSearchMessage() != null) {
            GuiUtils.addLabel("Attention: " + ielement.getNoSearchMessage(), this, "gapx 10, span, width max, wrap", GuiStyle.SEARCH_RESULT_NO_SEARCH);
        }

        // adjust panel height
        popupParent.adjustHeight();

        revalidate();
        repaint();

    }

    /**
     * Display result with a more complex GUI, provided by element
     */
    private void displayAsComplexInteractionGui() {

        GuiUtils.throwIfNotOnEDT();

        displayMode = SUB_BOX;
        removeAll();

        JButton jbt = new JButton("Fermer");
        jbt.addActionListener(new CustomActionListener());
        add(jbt, "wrap");

        Component gui = ielement.getBlockGUI();

        // if element is hideable, retract it
        if (gui instanceof HideableBlockItem) {
            ((HideableBlockItem) gui).refresh(false, true);
        }

        add(gui, "width " + interactionGuiWidth);


        popupParent.adjustHeight();

        revalidate();
        repaint();

    }

    /**
     * Listen actions
     */
    private class CustomActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            // element is simple, run action in thread
            if (ielement.isDisplayableSimpleInSearch()) {
                ThreadManager.runLater(ielement);
            }

            // element is complex, extend it
            else {

                if (SIMPLE.equals(displayMode)) {
                    displayAsComplexInteractionGui();
                } else if (SUB_BOX.equals(displayMode)) {
                    displayAsSimpleSearchResult();
                }
            }

        }
    }
}
