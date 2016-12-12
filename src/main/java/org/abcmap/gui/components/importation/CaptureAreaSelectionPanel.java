package org.abcmap.gui.components.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.components.InvalidInputException;
import org.abcmap.gui.components.textfields.IntegerTextField;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Panel to select capture area. Capture area is describe in number of screen movement, from left to right and top to bottom
 */
public class CaptureAreaSelectionPanel extends JPanel implements HasListenerHandler<ActionListener> {

    private static final int SIDE_X = 8;
    private static final int SIDE_Y = 5;

    private ArrayList<SelectionButton> buttons;
    private IntegerTextField txtWidth;
    private IntegerTextField txtHeight;

    private JPanel btnPanel;
    private JPanel txtPanel;
    private ListenerHandler<ActionListener> listenerHandler;

    public CaptureAreaSelectionPanel() {
        super(new MigLayout("insets 0, gap 5"));

        listenerHandler = new ListenerHandler<>();

        // button controls
        btnPanel = new JPanel(new MigLayout("insets 4, gap 4"));
        buttons = new ArrayList<>();
        ButtonActionListener bal = new ButtonActionListener();

        for (int y = 0; y < SIDE_Y; y++) {
            for (int x = 0; x < SIDE_X; x++) {

                SelectionButton bt = new SelectionButton(x + 1, y + 1);
                bt.addActionListener(bal);

                buttons.add(bt);

                String constraints = "height 20px!, width 20px!,";
                if (x == SIDE_X - 1) {
                    constraints += "wrap";
                }

                btnPanel.add(bt, constraints);
            }
        }

        add(btnPanel, "span, wrap");

        txtWidth = new IntegerTextField(3);
        txtHeight = new IntegerTextField(3);

        TextFieldListener tflistener = new TextFieldListener();
        KeyAdapter.addListener(txtWidth, tflistener);
        KeyAdapter.addListener(txtHeight, tflistener);

        txtPanel = new JPanel(new MigLayout("insets 5, gap 5"));

        GuiUtils.addLabel("Largeur: ", txtPanel);
        txtPanel.add(txtWidth);

        GuiUtils.addLabel("Hauteur: ", txtPanel);
        txtPanel.add(txtHeight, "wrap");

        add(txtPanel, "wrap");

        // initialize at 1
        setValues(1, 1);
    }

    /**
     * Set values of panel and fire event
     *
     * @param width
     * @param height
     */
    public void setValues(int width, int height) {
        updateValuesWithoutFire(width, height);
        fireActionPerformed();
    }

    /**
     * Set values but do not fire event
     *
     * @param width
     * @param height
     */
    public void updateValuesWithoutFire(int width, int height) {

        GuiUtils.throwIfNotOnEDT();

        if (width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }

        changeButtonsValueWithoutFire(width, height);

        changeTextFieldsWithoutFire(width, height);

        refresh();
    }

    private void changeButtonsValueWithoutFire(int width, int height) {

        for (SelectionButton sb : buttons) {

            boolean selected = false;
            if (sb.getWidthValue() <= width && sb.getHeightValue() <= height) {
                selected = true;
            }

            if (sb.isSelected() != selected) {
                GuiUtils.setSelected(sb, selected);
                sb.repaint();
            }

        }
    }

    private void changeTextFieldsWithoutFire(int width, int height) {

        String strWidth = String.valueOf(width);
        GuiUtils.changeText(txtWidth, strWidth);

        String strHeight = String.valueOf(height);
        GuiUtils.changeText(txtHeight, strHeight);

    }

    /**
     * Listen input and change forms
     */
    private class TextFieldListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            // récupérer les caleurs saisies
            int width = -1;
            int height = -1;
            try {
                width = txtWidth.getIntegerValue();
                height = txtHeight.getIntegerValue();
            } catch (InvalidInputException e1) {
                // invalid input
                return;
            }

            setValues(width, height);

        }

    }

    private class ButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            SelectionButton source = (SelectionButton) e.getSource();
            int w = source.getWidthValue();
            int h = source.getHeightValue();

            setValues(w, h);

        }

    }

    private static class SelectionButton extends JToggleButton {

        private static final Color SELECTION_COLOR = Color.blue;
        private int widthValue = 0;
        private int heightValue = 0;

        public SelectionButton(int wVal, int hVal) {
            this.widthValue = wVal;
            this.heightValue = hVal;

            setToolTipText(wVal + " x " + hVal);
        }

        @Override
        protected void paintComponent(Graphics g) {

            // button is selected, paint it blue
            if (isSelected()) {
                Rectangle b = getBounds();
                b.x = 0;
                b.y = 0;
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(SELECTION_COLOR);
                g2d.fill(b);
            }

            // else normal painting
            else {
                super.paintComponent(g);
            }
        }

        public int getWidthValue() {
            return widthValue;
        }

        public int getHeightValue() {
            return heightValue;
        }

    }

    /**
     * Return current values or null
     *
     * @return
     */
    public Dimension getValues() {

        Integer width;
        Integer height;
        try {
            width = txtWidth.getIntegerValue();
            height = txtHeight.getIntegerValue();
        } catch (InvalidInputException e) {
            return null;
        }

        return new Dimension(width, height);
    }

    protected void fireActionPerformed() {
        listenerHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }

    public void refresh() {

        btnPanel.revalidate();
        btnPanel.repaint();

        txtWidth.repaint();
        txtHeight.repaint();

        this.revalidate();
        this.repaint();
    }

    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

}
