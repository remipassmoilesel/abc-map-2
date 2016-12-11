package org.abcmap.gui.tools;

import org.abcmap.core.draw.LayerElement;
import org.abcmap.core.managers.*;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.project.layer.AbstractLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MapTool extends MouseAdapter implements HasNotificationManager {

    protected NotificationManager observer;
    protected DrawManager drawm;
    protected GuiManager guim;
    protected ProjectManager projectm;
    protected MapManager mapm;
    protected CancelManager cancelm;
    protected String mode;
    //protected DrawProperties stroke;

    public MapTool() {
        this.drawm = MainManager.getDrawManager();
        this.guim = MainManager.getGuiManager();
        this.projectm = MainManager.getProjectManager();
        this.mapm = MainManager.getMapManager();
        this.cancelm = MainManager.getCancelManager();

        this.mode = null;

        this.observer = new NotificationManager(MapTool.this);
        MainManager.getDrawManager().getNotificationManager().addObserver(this);

    }

    protected void printForDebug(LayerElement elmt, Point p1) {

        /*

        PrintUtils.p();

        PrintUtils.pStackTrace(2);

        if (elmt != null) {

            Object[] keys = new Object[]{"Element", "Selected", "InteractionArea bounds",};

            Object[] values = new Object[]{elmt.getClass().getSimpleName(), elmt.isSelected(),
                    elmt.getInteractionArea().getBounds()};

            PrintUtils.pObjectAndValues(elmt, keys, values);

        }

        if (p1 != null) {

            // point appartient à forme
            String p1InIa = elmt.getInteractionArea() != null
                    ? Boolean.toString(elmt.getInteractionArea().contains(p1)) : "null IA";

            Object[] keys = new Object[]{"Point P1", "P1 in IA", "InteractionArea bounds",};

            Object[] values = new Object[]{p1.x + " : " + p1.y, p1InIa,
                    elmt.getInteractionArea().getBounds()};

            PrintUtils.pObjectAndValues(p1, keys, values);

        }

        */

    }

    /**
     * eturn current layer or null
     *
     * @return
     */
    protected AbstractLayer checkProjetAndReturnActiveLayer() {

        if (MainManager.getProjectManager().isInitialized() == false) {
            return null;
        }

        return MainManager.getProjectManager().getProject().getActiveLayer();

    }

    /**
     * Return true if project is initialized and if left click was used
     *
     * @param arg0
     * @return
     */
    protected boolean checkProjectAndLeftClick(MouseEvent arg0) {

        if (SwingUtilities.isLeftMouseButton(arg0) == false) {
            return false;
        }

        if (MainManager.getProjectManager().isInitialized() == false) {
            return false;
        }

        return true;
    }

    /**
     * Return true if project is initialized and if right click was used
     *
     * @param arg0
     * @return
     */
    protected boolean checkProjectAndRightClick(MouseEvent arg0) {

        if (SwingUtilities.isRightMouseButton(arg0) == false) {
            return false;
        }

        if (projectm.isInitialized() == false) {
            return false;
        }

        return true;

    }

    protected void unselectAllIfCtrlNotPressed(MouseEvent arg0) {
        if (arg0.isControlDown() == false) {
            projectm.getProject().setAllElementsSelected(false);
        }
    }

    public void setToolMode(String mode) {
        this.mode = mode;
    }

    public String getToolMode() {
        if (mode == null) {
            return null;
        } else {
            return new String(mode);
        }
    }

    @Override
    public NotificationManager getNotificationManager() {
        return observer;
    }

    /**
     * Optionnal display by tool
     *
     * @param g2d
     */
    public void drawOnCanvas(Graphics2D g2d) {
    }

    /**
     * Tool will be soon stopped, do cleanup
     */
    public void stopWorking() {
    }

}