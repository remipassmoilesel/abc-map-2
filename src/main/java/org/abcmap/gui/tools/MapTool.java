package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.draw.builder.AbmSimpleFeatureBuilder;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.*;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.gui.components.map.CachedMapPane;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.Style;
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
 * All utilities and helpers refer to main map panel
 */
public abstract class MapTool extends MouseAdapter implements HasEventNotificationManager {

    protected final static GeometryFactory geom = GeoUtils.getGeometryFactory();
    protected final static StyleFactory sf = FeatureUtils.getStyleFactory();
    protected final static FilterFactory2 ff = FeatureUtils.getFilterFactory();

    protected static final CustomLogger logger = LogManager.getLogger(MapTool.class);

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
     * This method call repaint() after
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
     * Delete active layer cache if possible, and repaint map.
     */
    protected void deleteActiveLayerCacheAndUpdateMap() {
        deleteActiveLayerCacheAndUpdateMap(null);
    }

    /**
     * Delete active layer cache if possible and repaint map.
     * <p>
     * If specified envelope is not null, just area around envelope will be deleted
     */
    protected void deleteActiveLayerCacheAndUpdateMap(ReferencedEnvelope env) {

        Project project = projectm.getProject();
        if (project == null) {
            return;
        }

        ThreadManager.runLater(() -> {
            project.deleteCacheForLayer(project.getActiveLayer().getId(), env);
            refreshMainMap();
        });

    }

    /**
     * Transform specified point and return a coordinate object
     *
     * @param p
     * @return
     */
    protected Coordinate screenPointToWorldCoordinate(Point2D p) {
        AffineTransform trans = getMainMapPane().getScreenToWorldTransform();
        Point2D worldPoint = trans.transform(p, null);
        return GeoUtils.point2DtoCoordinate(worldPoint);
    }

    /**
     * Transform specified point and return a coordinate object
     *
     * @param x
     * @param y
     * @return
     */
    protected Coordinate screenPointToWorldCoordinate(double x, double y) {
        return screenPointToWorldCoordinate(new Point2D.Double(x, y));
    }

    /**
     * Transform specified point and return a coordinate object
     *
     * @param p
     * @return
     */
    protected Coordinate screenPointToWorldCoordinate(Point p) {
        return screenPointToWorldCoordinate((Point2D) p);
    }

    /**
     * Transform specified point in a world envelope.
     * <p>
     * 2 px margin is used around screen position
     *
     * @param screenPos
     * @return
     */
    protected ReferencedEnvelope screenPointToWorldBounds(Point screenPos) {

        if (projectm.isInitialized() == false) {
            throw new IllegalArgumentException("Project not initialized");
        }

        Project project = projectm.getProject();
        AbmAbstractLayer activeLayer = projectm.getProject().getActiveLayer();

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

        return bbox;
    }


    /**
     * Transform specified point in a rectangular geometry
     * <p>
     * 2 px margin is used around screen position
     *
     * @param p
     * @return
     */
    protected Geometry screenPointToGeometryBounds(Point p) {
        return JTS.toGeometry(screenPointToWorldBounds(p));
    }

    /**
     * Get current world to screen transform associated with main map
     *
     * @return
     */
    public AffineTransform getWorldToScreenTransform() {
        return getMainMapPane().getWorldToScreenTransform();
    }

    /**
     * Get current screen to world transform associated with main map
     *
     * @return
     */
    public AffineTransform getScreenToWorldTransform() {
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
    protected ArrayList<SimpleFeature> getFeaturesFromProjectAroundMousePosition(Point screenPos) {

        if (projectm.isInitialized() == false) {
            logger.error("Project is not initialized");
            return null;
        }

        AbmAbstractLayer activeLayer = projectm.getProject().getActiveLayer();

        if (activeLayer instanceof AbmFeatureLayer == false) {
            logger.error("Wrong layer type");
            return null;
        }

        ReferencedEnvelope bbox = screenPointToWorldBounds(screenPos);

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

            if (i >= maxSelectedFeatureNumber) {
                logger.warning("Max number of features selected reached: " + maxSelectedFeatureNumber);
            }

            return result;
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * Get a collection of features localized around position (on main map)
     * <p>
     * Feature max number is limited
     *
     * Can return null if layer is not a feature layer or if error occur
     *
     * @param screenPos
     * @return
     */
    protected ArrayList<SimpleFeature> getFeaturesFromMemoryAroundMousePosition(Point screenPos) {

        if (projectm.isInitialized() == false) {
            logger.error("Project is not initialized");
            return null;
        }

        AbmAbstractLayer activeLayer = projectm.getProject().getActiveLayer();

        if (activeLayer instanceof AbmFeatureLayer == false) {
            logger.error("Wrong layer type");
            return null;
        }

        // get box to compare to features
        ReferencedEnvelope bbox = screenPointToWorldBounds(screenPos);
        Filter filter = AbmSimpleFeatureBuilder.getGeometryFilter(bbox);

        // get features and return it
        return getMainMapPane().getMemoryMapFeatures(filter);

    }

    /**
     * Add features to the memory layer of main map pane, to let user modify it
     * <p>
     * Add features via this method will remove all previous features from memory layer
     *
     * @param features
     */
    protected void setFeaturesModifiable(ArrayList<SimpleFeature> features) {

        if (projectm.isInitialized() == false) {
            throw new IllegalStateException("Project not initialized");
        }

        // create a special style to show modified features
        Style modificationStyle = FeatureUtils.createStyleFor(null, Color.red, null, 3);

        // set in memory content of main map
        getMainMapPane().setMemoryLayerContent(features, modificationStyle);

        // repaint main map
        repaintMainMap();

    }

    /**
     * Remove features from in memory layers, and push them in active layer
     *
     * @param features
     */
    protected void commitFeaturesChanges(java.util.List<SimpleFeature> features) {

        if (projectm.isInitialized() == false) {
            throw new IllegalStateException("Project not initialized");
        }

        AbmFeatureLayer activeLayer = (AbmFeatureLayer) projectm.getProject().getActiveLayer();

        // update features from active layer
        for (SimpleFeature feat : features) {
            activeLayer.updateFeature(feat);
        }

        // clear memory layer of main map pane
        getMainMapPane().clearMemoryLayer();

    }

    /**
     * Add features in memory to let user modify it
     */
    protected void cancelFeaturesChanges() {

        // clear memory layer of main map pane
        getMainMapPane().clearMemoryLayer();

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