package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.abcmap.core.project.Project;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;

/**
 * Tool to draw lines
 */
public class LineBuilder extends ShapeBuilder {

    private ArrayList<Coordinate> points;
    private SimpleFeature currentFeature;

    public LineBuilder(Project p) {
        super(p);
        points = new ArrayList<Coordinate>();
    }

    /**
     * Create a new line and add a point to the map
     *
     * @param firstPoint
     */
    public void newLine(Coordinate firstPoint) {

        throwIfDrawing();

        // store point to expand line later
        points.add(firstPoint);

        // display a point the first time
        Point pointShape = geometryFactory.createPoint(firstPoint);

        // store the current feature to change geometries later
        currentFeature = project.getActiveLayer().addShape(pointShape);
    }

    /**
     * Add a point to the current line
     *
     * @param nextPoint
     */
    public void addPoint(Coordinate nextPoint) {

        throwIfNotDrawing();

        // store point
        points.add(nextPoint);

        // create a new line
        LineString line = geometryFactory.createLineString(points.toArray(new Coordinate[points.size()]));

        // modify geometry
        currentFeature.setDefaultGeometry(line);

    }

    /**
     * Terminate the current line and reset tool
     *
     * @param endPoint
     */
    public void terminateLine(Coordinate endPoint) {

        throwIfNotDrawing();

        // store point
        points.add(endPoint);

        // create a new line
        LineString line = geometryFactory.createLineString(points.toArray(new Coordinate[points.size()]));

        // modify geometry
        currentFeature.setDefaultGeometry(line);

        resetTool();
    }

    /**
     * Reset tool
     */
    private void resetTool() {
        currentFeature = null;
        points = null;
    }

    /**
     * Stop drawing and remove the current line from project
     */
    public void cancelDrawing(){
        project.getActiveLayer().removeFeature(currentFeature);
        resetTool();
    }

    private void throwIfNotDrawing() {
        if (isDrawing() == false) {
            throw new DrawingException("Cannot perform this operation while not drawing");
        }
    }

    private void throwIfDrawing() {
        if (isDrawing() == true) {
            throw new DrawingException("Cannot perform this operation while drawing");
        }
    }

    public boolean isDrawing() {
        return currentFeature != null;
    }

}
