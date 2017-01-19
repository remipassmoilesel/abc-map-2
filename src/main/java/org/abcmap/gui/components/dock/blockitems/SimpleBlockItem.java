package org.abcmap.gui.components.dock.blockitems;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.*;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.components.dock.HasExpandableHelp;
import org.abcmap.gui.utils.FocusPainter;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleBlockItem extends JPanel implements HasExpandableHelp {

    protected InteractionElement interactionElmt;

    protected JComponent labelName;
    protected JLabel buttonHelp;
    protected Component bottomComponent;

    protected BlockItemHelpArea helpArea;
    protected boolean helpVisible = false;

    private boolean changeColorUnderFocus;
    private boolean focused;

    private FocusPainter focusPainter;

    public final static SimpleBlockItem create(InteractionElement ielmt,
                                               Component bottom) {
        SimpleBlockItem smi = new SimpleBlockItem(ielmt);
        smi.setBottomComponent(bottom);
        smi.reconstruct();
        return smi;
    }

    protected SimpleBlockItem() {

        // layout
        super(new MigLayout("insets 7 5 3 5"));

        focusPainter = new FocusPainter(GuiColors.PANEL_BACKGROUND);

        // caracteristiques
        setOpaque(true);
        helpVisible = false;

        interactionElmt = null;

        labelName = new HtmlLabel("No label");
        ((HtmlLabel) labelName).setStyle(GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);

        focused = false;
        changeColorUnderFocus = false;
        BackgroundColorListener bgColList = new BackgroundColorListener();
        labelName.addMouseListener(bgColList);
        labelName.addFocusListener(bgColList);

        buttonHelp = new JLabel(GuiIcons.DI_ITEM_HELP);
        buttonHelp.addMouseListener(new HelpActionListener());
        buttonHelp.setCursor(GuiCursor.HAND_CURSOR);

        helpArea = new BlockItemHelpArea();

        add(labelName, "width 230px!");
        add(buttonHelp, "wrap");

        expandHelp(helpVisible);

    }

    public SimpleBlockItem(InteractionElement elmt) {
        this();
        setInteractionElement(elmt);
        reconstruct(helpVisible);
    }

    public void reconstruct(boolean showHelp) {

        if (interactionElmt == null) {
            return;
        }

        // element is a lable
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

        labelName.revalidate();
        labelName.repaint();

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
            add(bottomComponent, "span, width 96%!, gaptop 10px, gapleft 10px, gapbottom 20px");
        }

        refresh();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (changeColorUnderFocus) {
            focusPainter.draw(g, this, isFocused());
        }
    }

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

    @Override
    public void expandHelp(boolean showHelp) {
        reconstruct(showHelp);
    }

    @Override
    public boolean isHelpExpanded() {
        return helpVisible;
    }

    public void addLabelListener(MouseListener ml) {
        labelName.addMouseListener(ml);
    }

    public void removeLabelListener(MouseListener ml) {
        labelName.removeMouseListener(ml);
    }

    private void setFocused(boolean val) {
        this.focused = val;
    }

    private boolean isFocused() {
        return focused;
    }

    public void changeColorUnderFocus(boolean val) {
        changeColorUnderFocus = val;
    }

    public void setInteractionElement(InteractionElement elmt) {
        this.interactionElmt = elmt;
    }

    public void reconstruct() {
        reconstruct(helpVisible);
    }

    public void refresh() {
        revalidate();
        repaint();
    }

}
