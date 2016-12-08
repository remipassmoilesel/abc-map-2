package org.abcmap.gui.components.geo;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.geo.GeoConstants;
import org.abcmap.core.geo.GeoInfoMode;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel where user can select which information are displayed next a shape
 */
public class GeoInfoForm extends JPanel implements HasListenerHandler<ActionListener> {

    private static final Integer[] PREDEFINED_SIZES = new Integer[]{10, 15, 20, 25, 50};

    private String gapLeft;
    private String largeWrap;
    private HtmlCheckbox chkPosDD;
    private HtmlCheckbox chkPosDMD;
    private ListenerHandler<ActionListener> listenerHandler;
    private HtmlCheckbox chkPosDMS;
    private HtmlCheckbox chkRange;
    private HtmlCheckbox chkAzimuth;
    private JComboBox<Integer> cbInfoTextSize;
    private HtmlCheckbox chkAnchor;

    private GeoInfoMode mode;

    public GeoInfoForm() {
        this(GeoInfoMode.ALL_INFORMATIONS);
    }

    public GeoInfoForm(GeoInfoMode mode) {
        super(new MigLayout("insets 0"));

        this.mode = mode;

        this.listenerHandler = new ListenerHandler<>();

        gapLeft = "gapleft 15px,";
        largeWrap = "wrap 10px,";

        chkPosDD = new HtmlCheckbox("Positions DD");
        chkPosDD.setActionCommand(GeoConstants.DISPLAY_DEGREES_DEC.toString());
        chkPosDD.addActionListener(new ActionDispatcher());

        chkPosDMD = new HtmlCheckbox("Positions DMD");
        chkPosDMD.setActionCommand(GeoConstants.DISPLAY_DEGREES_MINUTES_DEC
                .toString());
        chkPosDMD.addActionListener(new ActionDispatcher());

        chkPosDMS = new HtmlCheckbox("Positions DMS");
        chkPosDMS.setActionCommand(GeoConstants.DISPLAY_DEGREES_MINUTES_SEC
                .toString());
        chkPosDMS.addActionListener(new ActionDispatcher());

        chkRange = new HtmlCheckbox("Distances en m/km");
        chkRange.setActionCommand(GeoConstants.DISPLAY_DEGREES_MINUTES_SEC
                .toString());
        chkRange.addActionListener(new ActionDispatcher());

        chkAzimuth = new HtmlCheckbox("Azimuts");
        chkAzimuth.setActionCommand(GeoConstants.DISPLAY_DEGREES_MINUTES_SEC
                .toString());
        chkAzimuth.addActionListener(new ActionDispatcher());

        chkAnchor = new HtmlCheckbox("Dessiner l'ancre");
        chkAnchor
                .setActionCommand(GeoConstants.DISPLAY_LABEL_ANCHOR.toString());
        chkAnchor.addActionListener(new ActionDispatcher());

        constructPanel();
    }

    private void constructPanel() {

        if (mode == null)
            mode = GeoInfoMode.ALL_INFORMATIONS;

        removeAll();

        if (mode.isPosDD())
            add(chkPosDD, gapLeft + "wrap");

        if (mode.isPosDMD())
            add(chkPosDMD, gapLeft + "wrap");

        if (mode.isPosDMS())
            add(chkPosDMS, gapLeft + "wrap");

        if (mode.isRange())
            add(chkRange, gapLeft + "wrap");

        if (mode.isAzimuth())
            add(chkAzimuth, gapLeft + "wrap");

        if (mode.isDrawAnchor())
            add(chkAnchor, gapLeft + largeWrap);

        GuiUtils.addLabel("Taille du texte: ", this, "wrap");

        cbInfoTextSize = new JComboBox<Integer>(PREDEFINED_SIZES);
        cbInfoTextSize.setActionCommand(GeoConstants.DISPLAY_GEO_TEXT_SIZE
                .toString());
        cbInfoTextSize.setEditable(true);
        cbInfoTextSize.addActionListener(new ActionDispatcher());
        add(cbInfoTextSize, gapLeft + "wrap");
    }

    /**
     * Redirect all events to listeners
     */
    private class ActionDispatcher implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            listenerHandler.fireEvent(e);
        }
    }

    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

    public void changeInfomodeWithoutFire(GeoInfoMode infomode) {

        GuiUtils.setSelected(chkPosDD, infomode.isPosDD());
        GuiUtils.setSelected(chkPosDMD, infomode.isPosDMD());
        GuiUtils.setSelected(chkPosDMS, infomode.isPosDMS());
        GuiUtils.setSelected(chkRange, infomode.isRange());
        GuiUtils.setSelected(chkAzimuth, infomode.isAzimuth());
        GuiUtils.setSelected(chkAnchor, infomode.isDrawAnchor());

    }

    public void changeTextSizeWithoutFire(Integer size) {
        GuiUtils.changeWithoutFire(cbInfoTextSize, size);
    }

    public Integer getSelectedTextsize() {

        try {
            return Integer.valueOf(cbInfoTextSize.getSelectedItem().toString());
        } catch (Exception e) {
            return null;
        }

    }

    public GeoInfoMode getSelectedInfoMode() {

        boolean[] vals = new boolean[]{chkPosDD.isSelected(),
                chkPosDMD.isSelected(), chkPosDMS.isSelected(),
                chkRange.isSelected(), chkAzimuth.isSelected(),
                chkAnchor.isSelected()};

        return new GeoInfoMode(vals);
    }

    public void resetForm() {
        changeInfomodeWithoutFire(GeoInfoMode.NO_INFORMATIONS);
    }

}
