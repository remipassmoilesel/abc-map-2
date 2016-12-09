package org.abcmap.gui.components.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.robot.RobotConfiguration;
import org.abcmap.core.utils.Utils;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.components.InvalidInputException;
import org.abcmap.gui.components.textfields.DecimalTextField;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public class RobotImportOptionsPanel extends JPanel implements HasListenerHandler<ActionListener> {

    private Integer[] predefinedCoverings = new Integer[]{5, 10, 15, 20, 25, 30};

    private String wrap15;
    private String gapLeft;
    private RobotConfiguration[] predefinedConf;
    private DecimalTextField txtMovingDelay;
    private DecimalTextField txtHiddingDelay;
    private DecimalTextField txtCaptureDelay;
    private JComboBox<String> cbConfiguration;
    private JComboBox<Integer> cbCovering;

    private ListenerHandler<ActionListener> listenerHandler;

    private String[] predefinedConfNames;

    public RobotImportOptionsPanel() {

        super(new MigLayout("insets 0"));

        this.wrap15 = "wrap 15,";
        this.gapLeft = "gapleft 15px,";

        listenerHandler = new ListenerHandler<ActionListener>();

        // select import mode
        JRadioButton rdImportFromULC = new JRadioButton("Commencer du coin haut gauche");
        rdImportFromULC.setSelected(true);
        JRadioButton rdImportFromMiddle = new JRadioButton("Commencer du milieu");

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdImportFromULC);
        bg.add(rdImportFromMiddle);

        // retransmit radio btn events to observers
        RadioEventReporter radioEventReporter = new RadioEventReporter();

        rdImportFromMiddle.addActionListener(radioEventReporter);
        rdImportFromULC.addActionListener(radioEventReporter);

        add(rdImportFromULC, "wrap");
        add(rdImportFromMiddle, wrap15);

        // predefined settings
        GuiUtils.addLabel("Réglages prédéfinis: ", this, "wrap");
        predefinedConf = RobotConfiguration.getPredefinedConfigurations();
        predefinedConfNames = RobotConfiguration.getPredefinedConfigurationNames();
        cbConfiguration = new JComboBox<>(predefinedConfNames);
        cbConfiguration.addActionListener(new TextfieldSettingsUpdater());
        add(cbConfiguration, gapLeft + wrap15);

        // tile overlap
        GuiUtils.addLabel("Couverture entre images: ", this, "wrap");
        cbCovering = new JComboBox<>(predefinedCoverings);
        cbCovering.addActionListener(new CoveringEventReporter());
        add(cbCovering, gapLeft + "split 2");
        GuiUtils.addLabel("%", this, gapLeft + wrap15);

        // report text events to observers
        TextFieldEventReporter textFieldReporter = new TextFieldEventReporter();

        String sLabel = "s.";

        // time delays
        GuiUtils.addLabel("Délai avant déplacement:", this, "wrap");
        txtMovingDelay = new DecimalTextField(3);
        KeyAdapter.addListener(txtMovingDelay, textFieldReporter);
        add(txtMovingDelay, gapLeft + "split 2");
        GuiUtils.addLabel(sLabel, this, gapLeft + wrap15);

        GuiUtils.addLabel("Délai avant capture de l'écran:", this, "wrap");
        txtCaptureDelay = new DecimalTextField(3);
        KeyAdapter.addListener(txtCaptureDelay, textFieldReporter);
        add(txtCaptureDelay, gapLeft + "split 2");
        GuiUtils.addLabel(sLabel, this, gapLeft + wrap15);

        GuiUtils.addLabel("Délai avant masquage des fenêtres:", this, "wrap");
        txtHiddingDelay = new DecimalTextField(3);
        KeyAdapter.addListener(txtHiddingDelay, textFieldReporter);
        add(txtHiddingDelay, gapLeft + "split 2");
        GuiUtils.addLabel(sLabel, this, gapLeft + wrap15);

        setValues(RobotConfiguration.NORMAL_IMPORT);
    }

    private class CoveringEventReporter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            changeToCustomSettingsSetAndSave();
            fireEvent();
        }
    }

    private class RadioEventReporter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fireEvent();
        }
    }

    private void fireEvent() {
        listenerHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }

    private class TextFieldEventReporter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            changeToCustomSettingsSetAndSave();
            fireEvent();
        }
    }

    private void changeToCustomSettingsSetAndSave() {
        if (cbConfiguration.getSelectedIndex() != RobotConfiguration.CUSTOM_SETTINGS_INDEX) {
            GuiUtils.changeWithoutFire(cbConfiguration, predefinedConfNames[RobotConfiguration.CUSTOM_SETTINGS_INDEX]);
        }

        try {
            RobotConfiguration.CUSTOM_IMPORT.update(getValues());
        } catch (InvalidInputException e) {
            // Log.debug(e);
        }
    }

    private class TextfieldSettingsUpdater implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ev) {
            RobotConfiguration rconf = predefinedConf[cbConfiguration.getSelectedIndex()];
            setValues(rconf);
        }

    }

    public void setValues(RobotConfiguration rconf) {

        GuiUtils.throwIfNotOnEDT();

        RobotConfiguration.CUSTOM_IMPORT.update(rconf);

        String captureDelay = String.valueOf(rconf.getCaptureDelay() / 1000f);
        if (txtCaptureDelay.isFocusOwner() == false) {
            GuiUtils.changeText(txtCaptureDelay, captureDelay);
        }

        String movingDelay = String.valueOf(rconf.getMovingDelay() / 1000f);
        if (txtMovingDelay.isFocusOwner() == false) {
            GuiUtils.changeText(txtMovingDelay, movingDelay);
        }

        String hiddingDelay = String.valueOf(rconf.getHiddingDelay() / 1000f);
        if (txtHiddingDelay.isFocusOwner() == false) {
            GuiUtils.changeText(txtHiddingDelay, hiddingDelay);
        }

        int covering = Math.round(rconf.getCovering() * 100);

        if (Utils.safeEquals(cbCovering.getSelectedItem(), covering) == false) {
            GuiUtils.changeWithoutFire(cbCovering, covering);
        }

        // notification
        fireEvent();
    }


    public RobotConfiguration getValues() throws InvalidInputException {

        RobotConfiguration rconf = new RobotConfiguration("SELECTED - " + cbConfiguration.getSelectedItem());

        try {
            rconf.setCaptureDelay(getCaptureDelayMs());
            rconf.setCovering(getCovering());
            rconf.setMovingDelay(getMovingDelayMs());
            rconf.setHiddingDelay(getHidingDelayMs());
        } catch (Exception e) {
            // Log.debug(e);
            throw new InvalidInputException(e);
        }

        return rconf;

    }

    private Integer getHidingDelayMs() throws InvalidInputException {
        return Math.round(txtHiddingDelay.getFloatValue() * 1000);
    }

    /**
     * Retourne le taux de recouvrement saisi
     *
     * @return
     * @throws InvalidInputException
     */
    private float getCovering() throws InvalidInputException {
        try {
            return ((Integer) cbCovering.getSelectedItem()) / 100f;
        } catch (Exception e) {
            throw new InvalidInputException();
        }
    }

    private int getMovingDelayMs() throws InvalidInputException {
        return Math.round(txtMovingDelay.getFloatValue() * 1000);
    }

    private int getCaptureDelayMs() throws InvalidInputException {
        return Math.round(txtCaptureDelay.getFloatValue() * 1000);
    }

    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

}
