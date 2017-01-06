package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Draw line on map with current style
 */
public class SelectionTool extends MapTool {

    /**
     * Features currently been selected
     */
    private final ArrayList<SimpleFeature> selectedFeatures;

    /**
     * Utility used to draw rectangles, for area selection
     */
    private final SimpleRectangleDesigner rectangleSelectionDesigner;

    /**
     * If true, features are moving
     */
    private boolean moving;

    /**
     * Last position of mouse, used to compute moves
     */
    private Point lastMousePosition;

    /**
     * Bounds of feature before moving them
     */
    private ReferencedEnvelope boundsBeforeMove;

    public SelectionTool() {
        super();
        moving = false;
        selectedFeatures = new ArrayList<>();
        rectangleSelectionDesigner = new SimpleRectangleDesigner();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        // retrieve active layer
        AbmFeatureLayer activeLayer = getActiveFeatureLayerIfLeftClickOrShowMessage(e);
        if (activeLayer == null) {
            return;
        }

        // get features around click position
        ArrayList<SimpleFeature> newSelection = getFeaturesFromProjectAroundMousePosition(e.getPoint());

        // no features or error, cancel feature changes and return
        if (newSelection == null || newSelection.size() < 1) {

            // cancel selection and feature changes only if control NOT down
            if (e.isControlDown() == false) {
                clearMemoryLayer();
                selectedFeatures.clear();
                repaintMainMap();
            }

            return;
        }

        // ctrl not down: remove previous features from selection
        if (e.isControlDown() == false) {
            selectedFeatures.clear();
        }

        // add selected new features to global selection
        selectedFeatures.addAll(newSelection);

        // make all modifiable
        setFeaturesModifiable(selectedFeatures);

        boundsBeforeMove = GeoUtils.getBoundsFromFeatureList(selectedFeatures);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        // we are not moving shapes now
        if (moving == false) {

            // check if mouse is on a shape in memory, if on start moving
            ArrayList<SimpleFeature> featuresUnderMouse = getFeaturesFromMemoryAroundMousePosition(e.getPoint());
            if (featuresUnderMouse != null && featuresUnderMouse.size() > 0) {
                moving = true;
            }

            // else draw a rectangle for multiple selection
            if (moving == false) {
                rectangleSelectionDesigner.mouseDragged(e);
            }
        }

        // we are moving, translate shapes
        if (moving == true) {

            // first move: keep mouse position and return
            if (lastMousePosition == null) {
                lastMousePosition = e.getPoint();
                return;
            }

            // compute world unit move
            Point currentPos = e.getPoint();
            double scale = getMainMapPane().getScale();
            double mX = (currentPos.getX() - lastMousePosition.getX()) * scale;
            double mY = (currentPos.getY() - lastMousePosition.getY()) * scale;

            // apply move to all geometries
            for (SimpleFeature feat : selectedFeatures) {

                Geometry g = (Geometry) feat.getDefaultGeometry();

                g.apply(new CoordinateFilter() {
                    @Override
                    public void filter(Coordinate coord) {
                        coord.x += mX;
                        coord.y -= mY;
                    }
                });

                // notify change
                g.geometryChanged();
            }

            // keep last position
            lastMousePosition = currentPos;

        }

        repaintMainMap();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        // we was moving features, commit changes and refresh map
        if (moving && selectedFeatures.size() > 0) {

            // commit changes
            commitFeaturesChanges(selectedFeatures);

            // compute total bounds including old area and new area of map
            ReferencedEnvelope boundsAfterMove = GeoUtils.getBoundsFromFeatureList(selectedFeatures);
            boundsAfterMove.include(boundsBeforeMove);

            // refresh map
            deleteActiveLayerCacheAndUpdateMap(boundsAfterMove);

        }

        // we was drawing a rectangle area for selection
        else if (rectangleSelectionDesigner.isWorking()) {

            // finish rectangle and get it
            rectangleSelectionDesigner.mouseReleased(e);

            // translate screen rectangle in world envelope
            Rectangle screenSelection = rectangleSelectionDesigner.getRectangle();
            ReferencedEnvelope worldSelection = screenRectangleToWorldEnvelope(screenSelection);

            // get features in world envelope
            ArrayList<SimpleFeature> features = getFeaturesFromProjectInEnvelope(worldSelection, false);

            // remove old features if control is not down
            if(e.isControlDown() == false){
                selectedFeatures.clear();
            }

            // add feature in selection and in memory layer
            selectedFeatures.addAll(features);
            if(selectedFeatures.size() > 0){
                setFeaturesModifiable(selectedFeatures);
            }

            // no features selected, clear in memory layer
            else {
                clearMemoryLayer();
            }

        }

        // reset flags
        lastMousePosition = null;
        moving = false;
        rectangleSelectionDesigner.resetRectangle();

        repaintMainMap();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        // repaint main map to show rectangle selection if needed
        if (rectangleSelectionDesigner.isWorking()) {
            repaintMainMap();
        }
    }

    @Override
    public void drawOnMainMap(Graphics2D g2d) {
        super.drawOnMainMap(g2d);

        // draw rectangle selection if needed
        rectangleSelectionDesigner.draw(g2d);
    }
}
