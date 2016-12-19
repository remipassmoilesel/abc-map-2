package org.abcmap.gui.tools;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.managers.*;
import org.abcmap.core.project.layers.AbstractLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MapTool extends MouseAdapter implements HasEventNotificationManager {

    protected EventNotificationManager observer;
    protected DrawManager drawm;
    protected GuiManager guim;
    protected ProjectManager projectm;
    protected MapManager mapm;
    protected CancelManager cancelm;
    protected DialogManager dialm;

    protected String mode;


    public MapTool() {
        this.drawm = MainManager.getDrawManager();
        this.guim = MainManager.getGuiManager();
        this.projectm = MainManager.getProjectManager();
        this.mapm = MainManager.getMapManager();
        this.cancelm = MainManager.getCancelManager();
        this.dialm = MainManager.getDialogManager();

        this.mode = null;

        this.observer = new EventNotificationManager(MapTool.this);
        MainManager.getDrawManager().getNotificationManager().addObserver(this);

    }

    /**
     * Check if project is initialized. If it is, return true, else return false and show a message to user
     *
     * @return
     */
    protected boolean checkProjectOrShowMessage() {
        if (projectm.isInitialized() == false) {
            dialm.showErrorInBox("Vous devez créer un projet ou ouvrir un projet existant");
            return false;
        }

        return true;
    }

    /**
     * Return current layer or null
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
    public EventNotificationManager getNotificationManager() {
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