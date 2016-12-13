package org.abcmap.gui.ie.position;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.MapEvent;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.EventListener;
import org.abcmap.gui.components.geo.CoordinatesPanel;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MoveElementsByCoordinates extends InteractionElement {

    private CoordinatesPanel cpanel;
    private ElementMover mover;
    private MoveElementsUpdater updater;

    public MoveElementsByCoordinates() {

        this.label = "Déplacement par coordonnées";
        this.help = "Déplacer des élements sélectionnés en saisissant des coordonnées dans les champs ci-dessous.";

        // affichage particulier pendant une recherche
        this.displaySimplyInSearch = false;

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 5"));
        GuiUtils.addLabel("Référence de positionnement: ", panel, "wrap");

        ReferenceChoiceAL ral = new ReferenceChoiceAL();

        JRadioButton byCenter = new JRadioButton("Par le centre");
        byCenter.setActionCommand(ElementMover.MOVE_BY_CENTER);
        byCenter.setSelected(true);
        byCenter.addActionListener(ral);
        panel.add(byCenter, "wrap");

        JRadioButton byUlc = new JRadioButton("Par le coin haut gauche");
        byUlc.setActionCommand(ElementMover.MOVE_BY_ULC);
        byUlc.addActionListener(ral);
        panel.add(byUlc, "wrap 10px");

        ButtonGroup bg = new ButtonGroup();
        bg.add(byCenter);
        bg.add(byUlc);

        GuiUtils.addLabel("Coordonnées: ", panel, "wrap");

        cpanel = new CoordinatesPanel();
        cpanel.getListenerHandler().add(mover);
        panel.add(cpanel, "wrap");

        mover = new ElementMover();

        updater = new MoveElementsUpdater();
        notifm.setDefaultListener(updater);

        projectm.getNotificationManager().addObserver(this);

        mapm.getNotificationManager().addObserver(this);

        updater.updateDegreeForm();

        return panel;

    }

    private class ElementMover implements Runnable, ActionListener {

        public static final String MOVE_BY_CENTER = "MOVE_BY_CENTER";
        public static final String MOVE_BY_ULC = "MOVE_BY_ULC";
        private String mode;

        public ElementMover() {
            this.mode = MOVE_BY_CENTER;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }

        @Override
        public void run() {

        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }
    }

    private class MoveElementsUpdater implements EventListener {

        @Override
        public void notificationReceived(Event arg) {

            if (arg instanceof MapEvent) {
                updateDegreeForm();
            } else if (arg instanceof ProjectEvent) {
                updateCoordinateForm();
            }
        }

        public void updateCoordinateForm() {
            // TODO Auto-generated method stub

        }

        public void updateDegreeForm() {
            boolean georefMode = mapm.isGeoreferencementEnabled();
            if (cpanel != null && cpanel.isDegreesFormEnabled() != georefMode) {
                cpanel.setDegreesFormEnabled(georefMode);
            }
        }

    }

    private class ReferenceChoiceAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String mode = e.getActionCommand();
            if (mover.getMode() != null
                    && mover.getMode().equals(mode) == false) {
                mover.setMode(mode);
            }

        }

    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}
