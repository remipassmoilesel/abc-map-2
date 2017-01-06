package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

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
     * If true, features are moving
     */
    private boolean moving;

    /**
     * Last position of mouse, used to compute moves
     */
    private Point lastMousePosition;

    public SelectionTool() {
        super();
        moving = false;
        selectedFeatures = new ArrayList<SimpleFeature>();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        // retrieve active layer
        AbmFeatureLayer activeLayer = getActiveFeatureLayerIfLeftClickOrShowMessage(e);
        if (activeLayer == null) {
            return;
        }

        // get features around click position
        ArrayList<SimpleFeature> newSelection = getFeaturesFromProjectAroundMousePosition(e.getPoint());

        // no features or error, cancel feature changes and return
        if (newSelection == null || newSelection.size() < 1) {

            // cancel selecton and feature changes only if control NOT down
            if (e.isControlDown() == false) {
                cancelFeaturesChanges();
                selectedFeatures.clear();
            }

            return;
        }

        // ctrl not down: remove previous features
        if (e.isControlDown() == false) {
            selectedFeatures.clear();
        }

        // add selected new features to global selection
        selectedFeatures.addAll(newSelection);

        // make all modifiable
        setFeaturesModifiable(selectedFeatures);


    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        // first move: keep mouse position and return
        if (lastMousePosition == null) {
            lastMousePosition = e.getPoint();
            return;
        }

        // we are not moving shapes now
        if(moving == false){

            // check if mouse is on a shape, if on start moving
            ArrayList<SimpleFeature> featuresUnderMouse = getFeaturesFromMemoryAroundMousePosition(e.getPoint());
            if (featuresUnderMouse != null && featuresUnderMouse.size() > 0) {
                moving = true;
            }
        }

        // we are moving, translate shapes
        if(moving == true){

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

            // repaint in memory layer map
            repaintMainMap();

            // keep last position
            lastMousePosition = currentPos;

        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        // we was moving features, commit changes and refresh map
        if (moving && selectedFeatures.size() > 0) {

            // commit changes
            commitFeaturesChanges(selectedFeatures);

            // refresh map
            ReferencedEnvelope listArea = GeoUtils.getBoundsFromFeatureList(selectedFeatures);
            deleteActiveLayerCacheAndUpdateMap(listArea);

            // reset flags
            lastMousePosition = null;
            moving = false;
        }

    }

}
