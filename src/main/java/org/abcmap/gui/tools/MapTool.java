package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.draw.builder.AbmSimpleFeatureBuilder;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.*;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.gui.components.map.CachedMapPane;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Tool that can be used to draw shapes on main map panel
 * <p>
 * All helpers refer to main map panels
 */
public abstract class MapTool extends MouseAdapter implements HasEventNotificationManager {

    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory2 ff = FeatureUtils.getFilterFactory();

    private static final CustomLogger logger = LogManager.getLogger(MapTool.class);

    protected EventNotificationManager observer;
    protected DrawManager drawm;
    protected GuiManager guim;
    protected ProjectManager projectm;
    protected MapManager mapm;
    protected CancelManager cancelm;
    protected DialogManager dialm;

    protected int maxSelectedFeatureNumber;
    protected String mode;


    public MapTool() {
        this.drawm = Main.getDrawManager();
        this.guim = Main.getGuiManager();
        this.projectm = Main.getProjectManager();
        this.mapm = Main.getMapManager();
        this.cancelm = Main.getCancelManager();
        this.dialm = Main.getDialogManager();

        this.mode = null;

        this.observer = new EventNotificationManager(MapTool.this);
        drawm.getNotificationManager().addObserver(this);

        this.maxSelectedFeatureNumber = 1000;

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
    protected AbmAbstractLayer getActiveLayerOrShowMessage() {

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
    protected AbmAbstractLayer getActiveLayerIfLeftClickOrShowMessage(MouseEvent arg0) {

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
    protected AbmFeatureLayer getActiveFeatureLayerIfLeftClickOrShowMessage(MouseEvent arg0) {

        Project p = getProjectIfLeftClickOrShowMessage(arg0);
        if (p == null) {
            return null;
        }

        // this layer cannot be modified
        if (p.getActiveLayer() instanceof AbmFeatureLayer == false) {
            dialm.showErrorInBox("Vous ne pouvez pas modifier cette couche");
            return null;
        }

        return (AbmFeatureLayer) p.getActiveLayer();
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

        GuiUtils.throwIfOnEDT();

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
    protected Coordinate mainMapScreenToWorldCoordinate(Point2D p) {
        AffineTransform trans = getMainMapPane().getScreenToWorldTransform();
        Point2D worldPoint = trans.transform(p, null);
        return GeoUtils.point2DtoCoordinate(worldPoint);
    }

    /**
     * Get point, transform it and return a coordinate object
     *
     * @param x
     * @param y
     * @return
     */
    protected Coordinate mainMapScreenToWorldCoordinate(double x, double y) {
        return mainMapScreenToWorldCoordinate(new Point2D.Double(x, y));
    }

    /**
     * Get point, transform it and return a coordinate object
     *
     * @param p
     * @return
     */
    protected Coordinate mainMapScreenToWorldCoordinate(Point p) {
        return mainMapScreenToWorldCoordinate((Point2D) p);
    }

    /**
     * Get current world to screen transform associated with main map
     *
     * @return
     */
    public AffineTransform getMainMapWorldToScreen() {
        return getMainMapPane().getWorldToScreenTransform();
    }

    /**
     * Get current screen to world transform associated with main map
     *
     * @return
     */
    public AffineTransform getMainMapScreenToWorld() {
        return getMainMapPane().getScreenToWorldTransform();
    }

    /**
     * Get a collection of features localized around position (on main map)
     * <p>
     * Feature max number is limited
     *
     * @param screenPos
     * @return
     */
    protected ArrayList<SimpleFeature> getFeaturesAroundMousePosition(Point screenPos) {

        if (projectm.isInitialized() == false) {
            logger.error("Project is not initialized");
            return null;
        }

        Project project = projectm.getProject();
        AbmAbstractLayer activeLayer = projectm.getProject().getActiveLayer();

        if (activeLayer instanceof AbmFeatureLayer == false) {
            logger.error("Wrong layer type");
            return null;
        }

        // Construct a 5x5 pixel rectangle centred on the mouse click position
        Rectangle screenRect = new Rectangle(screenPos.x - 2, screenPos.y - 2, 5, 5);

        // transform it in world unit
        AffineTransform screenToWorld = getMainMapPane().getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect, projectm.getProject().getCrs());

        // transform it to layer crs if needed
        if (activeLayer.getCrs().equals(project.getCrs()) == false) {

            logger.warning("CRS do not match, modifying bounding box. Original: " + bbox);

            try {
                bbox = bbox.transform(activeLayer.getCrs(), true);
            } catch (TransformException | FactoryException e) {
                logger.error(e);
            }

            logger.warning("CRS do not match, modifying bounding box. Modified: " + bbox);
        }

        Filter filter = AbmSimpleFeatureBuilder.getGeometryFilter(bbox);
        try {
            FeatureIterator iter = ((AbmFeatureLayer) activeLayer).getFeatures(filter).features();
            ArrayList<SimpleFeature> result = new ArrayList<>();
            int i = 0;
            while (iter.hasNext() && i < maxSelectedFeatureNumber) {
                result.add((SimpleFeature) iter.next());
                i++;
            }
            iter.close();
            return result;
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * Set tool mode, it should change the tool behavior
     *
     * @param mode
     */
    public void setToolMode(String mode) {
        this.mode = mode;
    }

    /**
     * Return current tool mode. Can be null.
     *
     * @return
     */
    public String getToolMode() {
        return mode;
    }


    protected void unselectAllIfCtrlNotPressed(MouseEvent arg0) {
        if (arg0.isControlDown() == false) {
            projectm.getProject().setAllElementsSelected(false);
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