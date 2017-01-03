package org.abcmap.gui.components.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.ImportManager;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.InvalidInputException;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.components.textfields.IntegerTextField;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

public class CropDimensionsPanel extends JPanel {

    private HtmlCheckbox chkActivateCropping;
    private IntegerTextField txtX;
    private IntegerTextField txtY;
    private IntegerTextField txtW;
    private IntegerTextField txtH;
    private JButton btnVisualConfig;
    private JButton btnCloseWindow;
    private Mode mode;
    private IntegerTextField[] allTextFields;
    private TextFieldEnablerAL textFieldEnabler;
    private CropActivationAL listener;
    private ImportManager importm;
    private ConfigurationManager confm;

    public enum Mode {
        WITH_CLOSE_WINDOW_BUTTON, WITH_VISUAL_CONFIG_BUTTON
    }

    public CropDimensionsPanel(Mode mode) {

        super(new MigLayout("insets 0"));

        this.mode = mode;

        this.importm = Main.getImportManager();
        this.confm = Main.getConfigurationManager();

        this.textFieldEnabler = new TextFieldEnablerAL();

        chkActivateCropping = new HtmlCheckbox("Activer le recadrage");
        chkActivateCropping.setSelected(true);

        add(chkActivateCropping, "span, growx, wrap 10px");
        chkActivateCropping.addActionListener(textFieldEnabler);

        txtX = new IntegerTextField(5);
        txtY = new IntegerTextField(5);
        txtW = new IntegerTextField(5);
        txtH = new IntegerTextField(5);
        this.allTextFields = new IntegerTextField[]{txtX, txtY, txtW, txtH};


        GuiUtils.addLabel("Zone à conserver: ", this, "span, wrap");

        JPanel panel2 = new JPanel(new MigLayout("insets 5 10 5 5"));

        GuiUtils.addLabel("x: ", panel2);
        panel2.add(txtX, "gapright 10px");

        GuiUtils.addLabel("y: ", panel2);
        panel2.add(txtY, "span, wrap");

        GuiUtils.addLabel("w: ", panel2);
        panel2.add(txtW);

        GuiUtils.addLabel("h: ", panel2);
        panel2.add(txtH, "span, wrap 8px");

        // panneau d'informations
        panel2.add(new JLabel(GuiIcons.CROP_INFORMATIONS), "span, align center");

        add(panel2, "wrap 10px");

        btnVisualConfig = new JButton("Configurer visuellement");
        btnCloseWindow = new JButton("Fermer cette fenêtre");

        construct(mode);

        revalidate();
        repaint();

    }

    public void construct(Mode mode) {

        GuiUtils.throwIfNotOnEDT();

        remove(btnVisualConfig);
        remove(btnCloseWindow);

        if (Mode.WITH_CLOSE_WINDOW_BUTTON.equals(mode)) {
            add(btnCloseWindow, "span, align center, wrap");
        } else {
            add(btnVisualConfig, "span, align center, wrap");
        }
    }

    public HtmlCheckbox getChkActivateCropping() {
        return chkActivateCropping;
    }

    private class TextFieldEnablerAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean val = chkActivateCropping.isSelected();
            setTextfieldsEnabled(val);
        }

    }

    /**
     * x,y,w,h
     *
     * @return
     */
    public IntegerTextField[] getTextFields() {
        return allTextFields;
    }

    /**
     * x, y, w, h
     *
     * @return
     * @throws InvalidInputException
     */
    public Integer[] getValues() throws InvalidInputException {

        Integer[] values = new Integer[4];
        int i = 0;
        for (IntegerTextField itf : allTextFields) {
            values[i] = itf.getIntegerValue();
            i++;
        }

        return values;
    }

    public Rectangle getRectangle() throws InvalidInputException {

        Rectangle r = new Rectangle();
        r.x = allTextFields[0].getIntegerValue();
        r.y = allTextFields[1].getIntegerValue();
        r.width = allTextFields[2].getIntegerValue();
        r.height = allTextFields[3].getIntegerValue();

        return r;
    }

    public void updateValuesWithoutFire(int x, int y, int w, int h) {

        // pas d'action hors de l'EDT
        GuiUtils.throwIfNotOnEDT();

        int[] values = new int[]{x, y, w, h};
        for (int i = 0; i < allTextFields.length; i++) {

            IntegerTextField txtField = allTextFields[i];
            if (txtField.isFocusOwner() == false) {
                GuiUtils.changeText(txtField, String.valueOf(values[i]));
            }
        }

    }

    public void updateChkCroppingWithoutFire(boolean val) {
        if (chkActivateCropping.isSelected() != val) {
            GuiUtils.setSelected(chkActivateCropping, val);
            setTextfieldsEnabled(val);
        }
    }

    public void updateValuesWithoutFire(Rectangle r) {
        updateValuesWithoutFire(r.x, r.y, r.width, r.height);
    }

    public JButton getBtnCloseWindow() {
        return btnCloseWindow;
    }

    public JButton getBtnVisualConfig() {
        return btnVisualConfig;
    }

    public void addListener(KeyListener listener) {

        GuiUtils.throwIfNotOnEDT();

        for (IntegerTextField itf : allTextFields) {
            KeyAdapter.addListener(itf, listener);
        }
    }

    public void removeDocumentListener(KeyListener listener) {

        GuiUtils.throwIfNotOnEDT();

        for (IntegerTextField itf : allTextFields) {
            KeyAdapter.removeListener(itf, listener);
        }
    }

    public void setTextfieldsEnabled(boolean val) {

        GuiUtils.throwIfNotOnEDT();

        for (IntegerTextField itf : allTextFields) {
            if (itf.isEnabled() != val) {
                itf.setEnabled(val);
            }
        }
    }

    public void activateCroppingListener(boolean value) {

        if (value) {
            listener = new CropActivationAL();
            chkActivateCropping.addActionListener(listener);
        } else {
            if (listener != null) {
                chkActivateCropping.removeActionListener(listener);
            }
        }

    }

    private class CropActivationAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            boolean val = ((HtmlCheckbox) e.getSource()).isSelected();

            if (confm.isCroppingEnabled() != val) {
                confm.setCroppingEnabled(val);
            }
        }

    }

}
