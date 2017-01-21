package org.abcmap.gui.utils;

import org.abcmap.core.draw.LayerElement;
import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventListener;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.components.color.ColorButton;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Utility which allow to update Swing forms programmatically
 *
 * @author remipassmoilesel
 */
public class FormUpdater extends ManagerTreeAccessUtil implements EventListener, Runnable, ActionListener {

    /**
     * Event filter: block notifications by event type
     */
    protected ArrayList<Class> eventClassFilter;

    /**
     * Event filter: block notifications by event name
     */
    protected ArrayList<String> eventNameFilter;

    /**
     * Tool filter: block notifications by active tool type
     */
    protected ArrayList<Class> toolFilters;

    /**
     * If true, project initialisation will be tested before process
     */
    protected boolean testProjectBeforeUpdate;

    public FormUpdater() {

        eventClassFilter = new ArrayList<>();
        eventNameFilter = new ArrayList<>();
        toolFilters = new ArrayList<>();

        testProjectBeforeUpdate = false;

    }

    /**
     * Method called on EDT to perform form updates
     * <p>
     * This method perform update on current thread
     */
    @Override
    public void run() {

        GuiUtils.throwIfNotOnEDT();

        // check filters
        if (checkFilters(null) == false) {
            return;
        }

        updateFormFields();
    }

    /**
     * Update fields on EDT
     * <p>
     * This method perform update on EDT, but not on this thread
     */
    public void updateAllLater() {

        if (checkFilters(null) == false) {
            return;
        }

        SwingUtilities.invokeLater(this);
    }

    /**
     * This method should perform changes on form elements
     * <p>
     * Do not call this method directly, prefer updateAllLater()
     */
    protected void updateFormFields() {

        // no actions out of EDT
        GuiUtils.throwIfNotOnEDT();

    }

    /**
     * Return first element selected or null
     *
     * @param filter
     * @return
     */
    protected LayerElement getFirstSelectedElement(Class filter) {
        return drawm().getFirstSelectedElement(filter);
    }

    /**
     * Return first selected element or null
     *
     * @return
     */
    protected LayerElement getFirstSelectedElement() {
        return drawm().getFirstSelectedElement();
    }

    /**
     * Return first selected element or null
     *
     * @return
     */
    protected LayerElement getFirstSelectedElement(ArrayList<Class<? extends LayerElement>> filters) {
        return drawm().getFirstSelectedElement(filters);
    }


    /**
     * Check specified filters and return true if it pass
     *
     * @param event
     * @return
     */
    private boolean checkFilters(Event event) {

        // check if project initialized
        if (testProjectBeforeUpdate) {
            if (projectm().isInitialized() == false) {
                return false;
            }
        }

        // filter by class if needed
        if (event != null && eventClassFilter.size() > 0) {
            if (eventClassFilter.contains(event.getClass()) == false) {
                return false;
            }
        }

        // filter by name if needed
        if (event != null && eventNameFilter.size() > 0) {
            if (eventNameFilter.contains(event.getName()) == false) {
                return false;
            }
        }

        // filter by tool enabled if needed
        if (drawm().getCurrentTool() != null && toolFilters.size() > 0) {
            if (toolFilters.contains(drawm().getCurrentTool().getClass()) == false) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void eventReceived(Event event) {

        // check filters
        if (checkFilters(event) == false) {
            return;
        }

        updateAllLater();
    }

    /**
     * Update all when action event received
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        updateAllLater();
    }

    /**
     * Add an event filter to block notifications
     *
     * @param filter
     */
    public void addEventClassFilter(Class filter) {
        eventClassFilter.add(filter);
    }

    /**
     * Add an event name filter to block notifications
     *
     * @param name
     */
    public void addEventNameFilter(String name) {
        eventNameFilter.add(name);
    }

    /**
     * Add a tool filter which will block notifications when tool class is not equals to specified class
     *
     * @param filter
     */
    public void addDrawingToolFilter(Class filter) {
        toolFilters.add(filter);
    }

    /**
     * Utility used to update form components without firing events
     * <p>
     * Components are updated only if value if different than current value
     *
     * @param comp
     * @param value
     */
    protected void updateComponentWithoutFire(ColorButton comp, Color value) {
        if (Utils.safeEquals(comp.getColor(), value) == false) {
            comp.setColor(value);
        }
        comp.revalidate();
        comp.repaint();
    }

    /**
     * Utility used to update form components without firing events
     * <p>
     * Components are updated only if value if different than current value
     *
     * @param comp
     * @param value
     */
    protected void updateComponentWithoutFire(JTextComponent comp, String value) {
        if (Utils.safeEquals(comp.getText(), value) == false) {
            GuiUtils.changeText(comp, value);
        }
        comp.revalidate();
        comp.repaint();
    }

    /**
     * Utility used to update form components without firing events
     * <p>
     * Components are updated only if value if different than current value
     *
     * @param comp
     * @param value
     */
    protected void updateComponentWithoutFire(HtmlCheckbox comp, boolean value) {
        if (Utils.safeEquals(comp.isSelected(), value) == false) {
            GuiUtils.setSelected(comp, value);
        }
        comp.revalidate();
        comp.repaint();
    }

    /**
     * Utility used to update form components without firing events
     * <p>
     * Components are updated only if value if different than current value
     *
     * @param comp
     * @param value
     */
    protected void updateComponentWithoutFire(AbstractButton comp, boolean value) {
        if (Utils.safeEquals(comp.isSelected(), value) == false) {
            GuiUtils.setSelected(comp, value);
        }
        comp.revalidate();
        comp.repaint();
    }

    /**
     * Utility used to update form components without firing events
     * <p>
     * Components are updated only if value if different than current value
     *
     * @param comp
     * @param value
     */
    protected void updateComponentWithoutFire(JComboBox comp, Object value) {
        if (Utils.safeEquals(comp.getSelectedItem(), value) == false) {
            GuiUtils.changeWithoutFire(comp, value);
        }
        comp.revalidate();
        comp.repaint();
    }

}
