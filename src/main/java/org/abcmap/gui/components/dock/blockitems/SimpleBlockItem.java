package org.abcmap.gui.components.dock.blockitems;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.*;
import org.abcmap.gui.components.dock.HasExpandableHelp;
import org.abcmap.gui.utils.FocusPainter;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimpleBlockItem extends JPanel implements HasExpandableHelp {

    /**
     * Interaction element associated withblock GUI
     */
    protected final InteractionElement interactionElmt;

    /**
     * Label where is displayed name of element
     */
    protected JComponent labelName;

    /**
     * Button used to unfold help
     */
    protected JLabel buttonHelp;

    /**
     * Component displayed at bottom of element, may containing more components
     */
    protected Component bottomComponent;

    /**
     * Area where is displayed help, if displayed
     */
    protected BlockItemHelpArea helpArea;

    /**
     * If true, help is displayed
     */
    protected boolean helpVisible = false;

    /**
     * If true, background color of this item will change on focus
     */
    private boolean changeColorUnderFocus;

    /**
     * If true, this item is focused
     */
    private boolean focused;

    /**
     * Utility used to paint focus background of element
     */
    private FocusPainter focusPainter;

    public final static SimpleBlockItem create(InteractionElement ielmt,
                                               Component bottom) {
        SimpleBlockItem smi = new SimpleBlockItem(ielmt);
        smi.setBottomComponent(bottom);
        smi.reconstruct();
        return smi;
    }

    public SimpleBlockItem(InteractionElement elmt) {
        super(new MigLayout("insets 7 5 3 5, gap 0"));

        setOpaque(true);

        helpVisible = false;
        focused = false;
        changeColorUnderFocus = false;
        interactionElmt = elmt;
        focusPainter = new FocusPainter(GuiColors.PANEL_BACKGROUND);

        // label where is displayed name of element
        labelName = new HtmlLabel("No label");
        ((HtmlLabel) labelName).setStyle(GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);
        BackgroundColorListener bgColList = new BackgroundColorListener();
        labelName.addMouseListener(bgColList);
        labelName.addFocusListener(bgColList);

        add(labelName, "width 90%!");

        // button used to unfold help
        buttonHelp = new JLabel(GuiIcons.DI_ITEM_HELP);
        buttonHelp.addMouseListener(new HelpActionListener());
        buttonHelp.setCursor(GuiCursor.HAND_CURSOR);
        helpArea = new BlockItemHelpArea();

        add(buttonHelp, "wrap 4px");

        expandHelp(helpVisible);
        reconstruct(helpVisible);
    }

    /**
     * Reconstruct component
     */
    public void reconstruct() {
        reconstruct(helpVisible);
    }

    /**
     * Reconstruct component. If showHelp is true, help space will be displayed
     *
     * @param showHelp
     */
    public void reconstruct(boolean showHelp) {

        if (interactionElmt == null) {
            throw new IllegalStateException("Interaction element is null");
        }

        // element is a label
        if (labelName instanceof HtmlLabel) {
            ((JLabel) labelName).setText(interactionElmt.getLabel());
        }

        // element is a button
        else if (labelName instanceof AbstractButton) {
            ((AbstractButton) labelName).setText(interactionElmt.getLabel());
        }

        // element not recognized
        else {
            throw new IllegalStateException("Cannot change text of label component class: " + labelName.getClass());
        }

        helpVisible = showHelp;

        remove(helpArea);
        if (bottomComponent != null) {
            remove(bottomComponent);
        }

        if (interactionElmt.getHelp() != null) {
            buttonHelp.setVisible(true);
            buttonHelp.setEnabled(true);
        } else {
            buttonHelp.setVisible(false);
            buttonHelp.setEnabled(false);
        }

        if (helpVisible && interactionElmt.getHelp() != null) {
            helpArea.setText(interactionElmt.getHelp());

            add(helpArea, "span, width 96%!, wrap");
            helpArea.refresh();
        }

        if (bottomComponent != null) {
            add(bottomComponent, "span, width 96%!, gaptop 7px, gapleft 7px, gapbottom 15px");
        }

        labelName.revalidate();
        labelName.repaint();

        refresh();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (changeColorUnderFocus) {
            focusPainter.draw(g, this, isFocused());
        }
    }

    /**
     * Set the bottom component of this element
     * <p>
     * Call reconstruct() after to apply changes
     *
     * @param comp
     */
    public void setBottomComponent(Component comp) {

        GuiUtils.throwIfNotOnEDT();

        if (bottomComponent != null) {
            remove(bottomComponent);
        }

        this.bottomComponent = comp;
    }

    /**
     * Show help
     */
    private class HelpActionListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            helpVisible = !helpVisible;
            reconstruct(helpVisible);
        }
    }

    /**
     * Change painting according to focus
     */
    private class BackgroundColorListener extends MouseAdapter implements
            FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            setFocused(true);
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            setFocused(false);
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setFocused(true);
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setFocused(false);
            repaint();
        }
    }

    /**
     * If set to true, element will change of background color if it is focused.
     *
     * @param val
     */
    public void changeColorUnderFocus(boolean val) {
        changeColorUnderFocus = val;
    }

    @Override
    public void expandHelp(boolean showHelp) {
        reconstruct(showHelp);
    }

    @Override
    public boolean isHelpExpanded() {
        return helpVisible;
    }


    private void setFocused(boolean val) {
        this.focused = val;
    }

    private boolean isFocused() {
        return focused;
    }


    /**
     * Repaint and revalidate component
     */
    public void refresh() {
        revalidate();
        repaint();
    }

}
