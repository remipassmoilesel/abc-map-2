package org.abcmap.gui.components.search;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.CustomComponent;
import org.abcmap.gui.components.dock.blockitems.FoldableBlockItem;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Component which allow to show results
 */
public class IESearchResultPanel extends JPanel {

    /**
     * This result is displayed as a simple clickable element
     */
    private static final String SIMPLE = "SIMPLE";

    /**
     * This result is displayed with complex GUI
     */
    private static final String SUB_BOX = "SUB_BOX";

    /**
     * Interaction element associated with this GUI
     */
    private InteractionElement ielement;

    /**
     * Optional parent notified when result change size on user interaction
     */
    private InteractivePopupDisplay popupParent;

    /**
     * Current display mode: simple or complex
     */
    private String displayMode;

    public IESearchResultPanel(InteractionElement ie) {
        this(ie, null);
    }

    public IESearchResultPanel(InteractionElement ie, InteractivePopupDisplay popupParent) {

        super(new MigLayout("insets 0, gap 0"));

        GuiUtils.throwIfNotOnEDT();

        this.ielement = ie;
        this.popupParent = popupParent;

        this.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

        // default behavior: element is displayed simple
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

        GuiUtils.addLabel(ielement.getLabel(), comp, "width 95%!", GuiStyle.SEARCH_RESULT_LABEL);

        this.add(comp, "width 98%!, wrap");

        // show keyboard shortcut
        if (ielement.getAccelerator() != null) {

            String str = "Raccourci: " + Utils.keystrokeToString(ielement.getAccelerator());

            GuiUtils.addLabel(str, this, "gapx 5px, width 98%!, wrap", GuiStyle.SEARCH_RESULT_TEXT);

        }

        // show help
        if (ielement.getHelp() != null) {
            GuiUtils.addLabel("Aide: " + ielement.getHelp(), this, "gapx 10px, span, width 95%!, wrap 10px", GuiStyle.SEARCH_RESULT_TEXT);
        }

        // show message if command is not available by search
        if (ielement.getNoSearchMessage() != null) {
            GuiUtils.addLabel("Attention: " + ielement.getNoSearchMessage(), this, "gapx 10px, span, width 95%!, wrap 10px", GuiStyle.SEARCH_RESULT_NO_SEARCH);
        }

        // adjust panel height
        if(popupParent != null){
            popupParent.adjustHeight();
        }

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

        // if element is foldable, retract it
        if (gui instanceof FoldableBlockItem) {
            ((FoldableBlockItem) gui).refresh(false, true);
        }

        add(gui, "width 95%!");

        // notify parent
        if(popupParent != null){
            popupParent.adjustHeight();
        }

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

            // element is complex, extend it or fold it
            else {

                // extend if
                if (SIMPLE.equals(displayMode)) {
                    displayAsComplexInteractionGui();
                }

                // fold it
                else if (SUB_BOX.equals(displayMode)) {
                    displayAsSimpleSearchResult();
                }
            }

        }
    }
}
