package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.managers.*;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.gui.components.map.CachedMapPane;
import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Tool that can be used to draw shapes on main map panel
 * <p>
 * All helpers refer to main map panels
 */
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
        drawm.getNotificationManager().addObserver(this);

    }

    /**
     * Check if project is initialized. If it is, return true, else return false and show a message to user
     *
     * @return
     */
    protected Project getProjectOrShowMessage() {

        if (projectm.isInitialized() == false) {
            dialm.showErrorInBox("Vous devez créer un projet ou ouvrir un projet existant");
            return null;
        }

        return projectm.getProject();
    }


    /**
     * Return current active layer or null. If project is not initialized, show a message.
     *
     * @return
     */
    protected AbstractLayer getActiveLayerOrShowMessage() {

        Project p = getProjectOrShowMessage();

        if (p == null) {
            return null;
        }

        return p.getActiveLayer();

    }

    /**
     * Return true if project is initialized and if left click was used
     *
     * @param arg0
     * @return
     */
    protected Project getProjectIfLeftClickOrShowMessage(MouseEvent arg0) {

        if (SwingUtilities.isLeftMouseButton(arg0) == false) {
            return null;
        }

        Project p = getProjectOrShowMessage();
        if (p == null) {
            return null;
        }

        return p;
    }

    /**
     * Return true if project is initialized and if left click was used
     *
     * @param arg0
     * @return
     */
    protected AbstractLayer getActiveLayerIfLeftClickOrShowMessage(MouseEvent arg0) {

        Project p = getProjectIfLeftClickOrShowMessage(arg0);
        if (p == null) {
            return null;
        }

        return p.getActiveLayer();
    }

    /**
     * Return true if project is initialized and if left click was used
     *
     * @param arg0
     * @return
     */
    protected FeatureLayer getActiveFeatureLayerIfLeftClickOrShowMessage(MouseEvent arg0) {

        Project p = getProjectIfLeftClickOrShowMessage(arg0);
        if (p == null) {
            return null;
        }

        // this layer cannot be modified
        if (p.getActiveLayer() instanceof FeatureLayer == false) {
            dialm.showErrorInBox("Vous ne pouvez pas modifier cette couche");
            return null;
        }

        return (FeatureLayer) p.getActiveLayer();
    }

    /**
     * Return current main map pane or null
     *
     * @return
     */
    protected CachedMapPane getMainMapPane() {
        return mapm.getMainMap();
    }

    /**
     * Refresh main map if possible
     * <p>
     * Refresh call repaint() after
     */
    protected void refreshMainMap() {
        if (getMainMapPane() == null) {
            return;
        }

        getMainMapPane().refreshMap();
    }

    /**
     * Repaint main map if possible
     */
    protected void repaintMainMap() {
        if (getMainMapPane() == null) {
            return;
        }

        getMainMapPane().repaint();
    }

    /**
     * Delete active layer cache if possible. Should be called before redraw map.
     */
    protected void deleteActiveLayerCache() {
        deleteActiveLayerCache(null);
    }

    /**
     * Delete active layer cache if possible. Should be called before redraw map.
     */
    protected void deleteActiveLayerCache(ReferencedEnvelope env) {

        // TODO
        //GuiUtils.throwIfOnEDT();

        Project project = projectm.getProject();
        if (project == null) {
            return;
        }

        project.deleteCacheForLayer(project.getActiveLayer().getId(), env);

    }


    /**
     * Get point, transform it and return a coordinate object
     *
     * @param p
     * @return
     */
    protected Coordinate screenToWorldCoordinate(Point2D p) {
        return screenToWorldCoordinate((Point) p);
    }

    /**
     * Get point, transform it and return a coordinate object
     *
     * @param p
     * @return
     */
    protected Coordinate screenToWorldCoordinate(Point p) {
        AffineTransform trans = getMainMapPane().getScreenToWorldTransform();
        Point2D worldPoint = trans.transform(p, null);
        return GeoUtils.point2DtoCoordinate(worldPoint);
    }

    /**
     * Get current world to screen transform associated with main map
     *
     * @return
     */
    public AffineTransform getMainMapWorldToScreen() {
        return getMainMapPane().getWorldToScreenTransform();
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
     * Optional display by tool
     *
     * @param g2d
     */
    public void drawOnMainMap(Graphics2D g2d) {
    }

    /**
     * Tool will be soon stopped, do cleanup
     */
    public void stopWorking() {
    }

}