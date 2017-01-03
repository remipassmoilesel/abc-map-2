package org.abcmap.gui.components.geo;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.MapManager;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.utils.GuiUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel which allow user to select a CRS
 */
public class CrsSelectionPanel extends JPanel implements HasListenerHandler<ActionListener> {

    private static final String OTHER_CRS = "OTHER_CRS";
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTextField txtResults;
    private CoordinateReferenceSystem selectedSystem;
    private ListenerHandler<ActionListener> listenerHandler;
    private JRadioButton rdOtherCRS;
    private List<String> predefinedCodes;
    private List<String> predefinedNames;
    private ArrayList<JRadioButton> predefinedRadioBtn;
    private ArrayList<CoordinateReferenceSystem> predefinedSystems;
    private MapManager mapm;

    public CrsSelectionPanel() {
        super(new MigLayout("insets 0"));

        mapm = Main.getMapManager();

        this.listenerHandler = new ListenerHandler<>();

        // default constraints
        String wrap15 = "wrap 15px, ";
        String gapLeft = "gap 15px, ";

        //predefinedCodes = Arrays.asList(GeoSystemsContainer.WEB_MERCATOR, GeoSystemsContainer.WGS_84);
        predefinedCodes = Arrays.asList("WGS84", "WGS84");
        predefinedNames = Arrays.asList("Web Mercator (applications web)", "WGS 84 (GPS, appareils GPS)");

        predefinedSystems = new ArrayList<>();
        predefinedRadioBtn = new ArrayList<>();

        // predefined simple CRS choices
        GuiUtils.addLabel("Choisir un système de coordonnées prédéfini: ", this, "span, wrap");

        ButtonGroup btnGroup = new ButtonGroup();
        for (int i = 0; i < predefinedCodes.size(); i++) {

            // recuperer code et nom du systeme
            String code = predefinedCodes.get(i);
            String name = predefinedNames.get(i);

            // creer un systeme et garder une reference vers le systeme
            CoordinateReferenceSystem sys = mapm.getCRS(code);
            if (sys == null) {
                throw new IllegalArgumentException("Invalid predefined code: "
                        + code);
            }
            predefinedSystems.add(sys);

            JRadioButton radio = new JRadioButton(name);
            radio.setActionCommand(code);
            radio.addActionListener(new SimpleCrsListener());

            add(radio, gapLeft + "span, wrap");
            btnGroup.add(radio);
            predefinedRadioBtn.add(radio);

        }

        // "others" radio button
        rdOtherCRS = new JRadioButton("Autre");
        rdOtherCRS.setActionCommand(OTHER_CRS);
        add(rdOtherCRS, gapLeft + "span, " + gapLeft + wrap15);
        btnGroup.add(rdOtherCRS);

        // search field
        GuiUtils.addLabel("Ou rechercher un système par code EPSG: ", this, "span, wrap");
        txtSearch = new JTextField();
        add(txtSearch, gapLeft + "width 80px");

        // seach button
        btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(new SearchCrsListener());
        add(btnSearch, "wrap");

        // display results
        txtResults = new JTextField();
        txtResults.setEditable(false);
        add(txtResults, "width 200px, span, " + gapLeft);

    }

    /**
     * Predefined CRS selection
     */
    private class SimpleCrsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String epsgCode = e.getActionCommand();
            if (epsgCode.equals(OTHER_CRS) == false) {
                selectedSystem = predefinedSystems.get(predefinedCodes.indexOf(epsgCode));
                fireEvent();
            }
        }

    }

    /**
     * CRS search
     */
    private class SearchCrsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String code = txtSearch.getText().trim();

            selectedSystem = mapm.getCRS(code);

            if (selectedSystem == null) {
                txtResults.setText("Code invalide");
            } else {
                displayNonPredefinedSystem(selectedSystem.getName().getCode());
                fireEvent();
            }
        }

    }

    /**
     * Display informations about predefined CRS on panel. No events fired.
     *
     * @param crsName
     */
    private void displayNonPredefinedSystem(String crsName) {

        GuiUtils.throwIfNotOnEDT();

        txtResults.setText(crsName);

        txtResults.setCaretPosition(0);

        rdOtherCRS.setSelected(true);

    }


    public CoordinateReferenceSystem getSelectedSystem() {
        return selectedSystem;
    }

    public void setSelectedSystem(CoordinateReferenceSystem system) {

        GuiUtils.throwIfNotOnEDT();

        updateSystemWithoutFire(system);

        fireEvent();

    }

    /**
     * Update CRS forms
     *
     * @param system
     */
    public void updateSystemWithoutFire(CoordinateReferenceSystem system) {

        GuiUtils.throwIfNotOnEDT();

        String code = MapManager.getEpsgCode(system);

        if (code == null) {

        }

        // system is predefined
        if (predefinedCodes.contains(code)) {
            for (JRadioButton btn : predefinedRadioBtn) {
                if (btn.getActionCommand().equals(code)) {
                    GuiUtils.setSelected(btn, true);
                    return;
                }
            }
        }

        // system is not predefined
        displayNonPredefinedSystem(system.getName().getCode());

    }

    private void fireEvent() {
        String code = selectedSystem != null ? selectedSystem.getName().getCode() : "null";
        listenerHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, code));
    }

    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

}
