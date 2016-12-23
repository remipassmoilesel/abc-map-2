package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.styles.StyleContainer;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;

/**
 * Abstract tool designed to draw lines or polygon
 */
public abstract class AbstractLineBuilder extends AbstractShapeBuilder {

    /**
     * List of points of shape
     */
    protected ArrayList<Coordinate> shapePoints;

    public AbstractLineBuilder(FeatureLayer layer, StyleContainer style) {
        super(layer, style);
    }

    /**
     * Get the geometry to add to the active layer
     *
     * @return
     */
    protected abstract Geometry getGeometry();

    /**
     * Create a new line and add a point to the map
     *
     * @param firstPoint
     */
    public SimpleFeature newLine(Coordinate firstPoint) {

        throwIfDrawing();

        shapePoints = new ArrayList<>();

        // store point to expand line later
        shapePoints.add(firstPoint);

        // display a point the first time
        Point pointShape = geometryFactory.createPoint(firstPoint);

        // store the current feature to change geometries later
        currentFeature = buildFeature(pointShape);

        return currentFeature;
    }

    /**
     * Add a point to the current line
     *
     * @param nextPoint
     */
    public SimpleFeature addPoint(Coordinate nextPoint) {

        throwIfNotDrawing();

        // store point
        shapePoints.add(nextPoint);

        // modify geometry
        currentFeature.setDefaultGeometry(getGeometry());

        return currentFeature;
    }

    /**
     * Terminate the current line and reset tool
     *
     * @param endPoint
     */
    public SimpleFeature terminateLine(Coordinate endPoint) {

        throwIfNotDrawing();

        // store last point
        shapePoints.add(endPoint);

        // update geometry
        currentFeature.setDefaultGeometry(getGeometry());

        // apply style attribute (to the last shape)
        applyStyle();

        // save current feature and layer, and get back the feature with correct id
        currentFeature = confirmDrawing();
        SimpleFeature returnVal = currentFeature;

        // reset all
        resetTool();

        return returnVal;
    }

    /**
     * Reset tool
     */
    private void resetTool() {
        currentFeature = null;
        shapePoints = null;
    }

    /**
     * Stop drawing and remove the current line from project
     */
    @Override
    public void cancelDrawing() {

        throwIfNotDrawing();

        resetTool();
    }

    public ArrayList<Coordinate> getPoints() {
        return new ArrayList<>(shapePoints);
    }

}
