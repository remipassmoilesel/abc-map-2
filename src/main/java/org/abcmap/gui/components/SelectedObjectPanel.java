package org.abcmap.gui.components;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.draw.LayerElement;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Display a sample of first object selected
 *
 * @author remipassmoilesel
 */
public class SelectedObjectPanel extends JPanel implements HasEventNotificationManager, HasListenerHandler<ActionListener> {

    /**
     * Panel where are drawn samples
     */
    private LayerElementSamplePanel sampleDisplayer;

    /**
     * Selection filters which can be used to reduce selection
     */
    private ArrayList filters;

    /**
     * Label where is displayed shape name
     */
    private JLabel lblTypeOfSample;

    /**
     * Maximum width of drawn sample
     */
    private int maxSampleWidth;

    private ListenerHandler<ActionListener> listenerHandler;
    private EventNotificationManager notifm;
    private ProjectManager projectm;

    public SelectedObjectPanel() {
        super(new MigLayout("insets 5"));

        this.projectm = MainManager.getProjectManager();
        this.listenerHandler = new ListenerHandler<>();
        this.filters = new ArrayList<>();

        this.maxSampleWidth = 40;

        sampleDisplayer = new LayerElementSamplePanel();
        add(sampleDisplayer, "width 50px!, height 50px!, gapright 10px");

        lblTypeOfSample = new JLabel();
        setLblTypeOfSampleText("Sélectionnez un objet");
        add(lblTypeOfSample);

        SelectionViewUpdater formUpdater = new SelectionViewUpdater();
        notifm = new EventNotificationManager(this);
        notifm.setDefaultListener(formUpdater);

        projectm.getNotificationManager().addObserver(this);

    }

    private void setLblTypeOfSampleText(String text) {
        lblTypeOfSample.setText("<html><b>" + text + "</b></html>");
    }

    /**
     * Met à jour la vue de sélection en fonction des changements de sléection
     * du projet.
     *
     * @author remipassmoilesel
     */
    private class SelectionViewUpdater extends FormUpdater {

        @Override
        protected void updateFields() {
            super.updateFields();

            LayerElement elmt = getFirstSelectedElement(filters);

            // no elements selected or error
            if (elmt == null) {
                sampleDisplayer.setSample(null);
                setLblTypeOfSampleText("Sélectionnez un objet");
                fireEvent();
                return;
            }

            // ask sample and display it
            LayerElement sample = elmt.getSample(maxSampleWidth, maxSampleWidth);
            sampleDisplayer.setSample(sample);

            // display element type
            setLblTypeOfSampleText(drawm.getReadableNameFor(sample.getClass()));

            fireEvent();
            refresh();
        }

    }

    /**
     * Display a sample of element
     */
    private class LayerElementSamplePanel extends JPanel {

        private LayerElement sample;

        public LayerElementSamplePanel() {
            this.sample = null;
            this.setBorder(BorderFactory.createLineBorder(Color.gray));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // draw sample
            if (sample != null) {
                sample.draw((Graphics2D) g, LayerElement.RENDER_FOR_PRINTING);
            }
        }

        public void setSample(LayerElement sample) {

            this.sample = sample;

            /**
             if (sample != null) {

             // center shape
             sample.refreshShape();
             Rectangle maxBounds = sample.getMaximumBounds();

             int px = (int) ((this.getWidth() - maxBounds.width) / 2f);
             int py = (int) ((this.getHeight() - maxBounds.height) / 2f);


             sample.setPosition(px, py);
             sample.refreshShape();
             }*/
        }

    }

    private void fireEvent() {
        listenerHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    public void refresh() {

        sampleDisplayer.revalidate();
        sampleDisplayer.repaint();

        lblTypeOfSample.revalidate();
        lblTypeOfSample.repaint();

        this.revalidate();
        this.repaint();
    }

    /**
     * Add element filter
     *
     * @param class1
     */
    public void addFilter(Class<? extends LayerElement> class1) {
        this.filters.add(class1);
    }

    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenerHandler;
    }

}
