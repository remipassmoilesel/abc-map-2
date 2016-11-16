package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.FeatureLayer;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;

/**
 * Abstract tool designed to draw lines or polygon
 */
public abstract class AbstractPolylineBuilder extends AbstractShapeBuilder {

    public AbstractPolylineBuilder(FeatureLayer layer) {
        super(layer);
    }

    protected ArrayList<Coordinate> shapePoints;

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

        shapePoints = new ArrayList<Coordinate>();

        // store point to expand line later
        shapePoints.add(firstPoint);

        // display a point the first time
        Point pointShape = geometryFactory.createPoint(firstPoint);

        // store the current feature to change geometries later
        currentFeature = getActiveLayer().addShape(pointShape);

        applyStyle();

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

        currentFeature = getActiveLayer().updateFeature(currentFeature);

        applyStyle();

        return currentFeature;
    }

    /**
     * Terminate the current line and reset tool
     *
     * @param endPoint
     */
    public SimpleFeature terminateLine(Coordinate endPoint) {

        throwIfNotDrawing();

        // store point
        shapePoints.add(endPoint);

        // modify geometry
        currentFeature.setDefaultGeometry(getGeometry());

        applyStyle();

        currentFeature = getActiveLayer().updateFeature(currentFeature);

        SimpleFeature returnVal = currentFeature;

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

        getActiveLayer().removeFeatures(currentFeature);

        resetTool();
    }

}
