package org.abcmap.gui.components.layers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventListener;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.utils.GuiUtils;

/**
 * Manage layers in project
 *
 * @author remipassmoilesel
 */
public class LayerSelectorPanel extends JPanel implements HasEventNotificationManager {

    private static final int SLIDER_MIN_VALUE = 0;
    private static final int SLIDER_MAX_VALUE = 100;

    private ProjectManager projectm;

    private DefaultListModel<AbstractLayer> listModel;
    private JList<AbstractLayer> jlist;
    private JSlider opacitySlider;
    private EventNotificationManager notifm;
    private FormUpdater formUpdater;
    private boolean showExceptions;

    public LayerSelectorPanel() {

        super(new MigLayout("insets 0"));

        this.showExceptions = false;

        this.projectm = MainManager.getProjectManager();

        // updates form values
        this.formUpdater = new FormUpdater();

        // listen project
        this.notifm = new EventNotificationManager(this);
        notifm.setDefaultListener(new LayerSelectorUpdater());
        projectm.getNotificationManager().addObserver(this);

        // selectable list of layers
        this.listModel = new DefaultListModel<>();
        jlist = new JList<>(listModel);
        jlist.setAlignmentY(Component.TOP_ALIGNMENT);
        jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
        jlist.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setVisibleRowCount(5);
        jlist.setCellRenderer(new LayerListRenderer());
        jlist.addListSelectionListener(new SelectionListener());

        // list is in scrollpane
        JScrollPane sp = new JScrollPane(jlist);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(sp, "span, width 90%, height 130px!, wrap 8px");

        // opacity setter
        GuiUtils.addLabel("Opacité du calque: ", this, "wrap");
        opacitySlider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN_VALUE,
                SLIDER_MAX_VALUE, 0);
        opacitySlider.addChangeListener(new SliderListener());
        add(opacitySlider, "grow, span, wrap 8px");

        // control buttons
        JButton up = new JButton(GuiIcons.LAYER_UP);
        up.addActionListener(new LayerListButtonsAL(LayerListButtonsAL.MOVE_UP));

        JButton down = new JButton(GuiIcons.LAYER_DOWN);
        down.addActionListener(new LayerListButtonsAL(
                LayerListButtonsAL.MOVE_DOWN));

        JButton rename = new JButton(GuiIcons.LAYER_RENAME);
        rename.addActionListener(new LayerListButtonsAL(
                LayerListButtonsAL.RENAME));

        JButton remove = new JButton(GuiIcons.LAYER_REMOVE);
        remove.addActionListener(new LayerListButtonsAL(
                LayerListButtonsAL.REMOVE));

        JButton newlayer = new JButton(GuiIcons.LAYER_ADD);
        newlayer.addActionListener(new LayerListButtonsAL(
                LayerListButtonsAL.NEW));

        JButton visibility = new JButton(GuiIcons.LAYER_VISIBILITY_BUTTON);
        visibility.addActionListener(new LayerListButtonsAL(
                LayerListButtonsAL.CHANGE_VISIBILITY));

        // buttons are in seprate panel
        JPanel subPanel = new JPanel(new MigLayout("insets 0"));
        String dim = "width 30!, height 30!";
        subPanel.add(newlayer, dim);
        subPanel.add(remove, dim);
        subPanel.add(up, dim);
        subPanel.add(down, dim);
        subPanel.add(rename, dim);
        subPanel.add(visibility, dim);

        add(subPanel);

        // refresh component
        revalidate();
        repaint();
    }

    /**
     * Listen mouse selection of layers
     */
    private class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            GuiUtils.throwIfNotOnEDT();

   /*         if (projectm.isInitialized() == false){
                return;
            }

            AbstractLayer activeLayer;
            try {
                activeLayer = projectm.getActiveLayer();
            } catch (AbstractLayerException e1) {

                // le projet n'est pas initialisé:
                // repeindre la liste vide puis retour
                if (showExceptions)
                    Log.debug(e1);
                jlist.repaint();
                return;
            }

            AbstractLayer lay = jlist.getSelectedValue();

            if (lay == null){
                return;
            }

            // selectionner le calque, uniquement si il n'est pas deja actif
            if (activeLayer.equals(lay) == false) {
                try {
                    projectm.setActiveLayer(lay);
                } catch (AbstractLayerException e1) {
                    if (showExceptions)
                        Log.debug(e1);
                }
            }*/

            refresh();

        }
    }

    /**
     * Listent opacity change of layers
     *
     * @author remipassmoilesel
     */
    public class SliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent arg0) {

            GuiUtils.throwIfNotOnEDT();

            if (projectm.isInitialized() == false) {
                return;
            }

            JSlider slider = (JSlider) arg0.getSource();
            float value = Float.valueOf(slider.getValue()) / 100;


            /*AbstractLayer lay;
            try {
                lay = projectm.getActiveLayer();
            } catch (AbstractLayerException e) {
                if (showExceptions)
                    Log.debug(e);
                return;
            }

            // appliquer la valeur d'opacité uniquement si différente
            if (lay.getOpacity() != value) {
                lay.setOpacity(value);
            }*/

        }
    }

    /**
     * Update form values
     */
    private class FormUpdater implements Runnable {

        @Override
        public void run() {

            GuiUtils.throwIfNotOnEDT();

            if (opacitySlider == null || jlist == null) {
                return;
            }

            // project is not initialized, set default values
            if (projectm.isInitialized() == false) {

                opacitySlider.setValue(0);
                opacitySlider.setEnabled(false);

                listModel.clear();
                jlist.setEnabled(false);

                return;
            }

           /* // project is initialized, show current values
            else {

                AbstractLayer activeLayer;
                try {
                    activeLayer = projectm.getActiveLayer();
                } catch (AbstractLayerException e) {
                    if (showExceptions)
                        Log.debug(e);
                    return;
                }

                float opacity = activeLayer.getOpacity();
                int newValue = Math.round(opacity * 100);
                if (newValue != opacitySlider.getValue()) {
                    GuiUtils.changeWithoutFire(opacitySlider, newValue);
                    opacitySlider.revalidate();
                    opacitySlider.repaint();
                }

                ArrayList<AbstractLayer> layers = projectm.getLayers();
                listModel.clear();
                for (int i = layers.size(); i > 0; i--) {
                    listModel.addElement(layers.get(i - 1));
                }

                jlist.revalidate();
                jlist.repaint();
            }*/

        }

    }

    private class LayerSelectorUpdater implements EventListener {
        @Override
        public void notificationReceived(Event arg) {
            SwingUtilities.invokeLater(formUpdater);
        }
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    public void refresh() {
        this.repaint();
        this.revalidate();
    }

}
