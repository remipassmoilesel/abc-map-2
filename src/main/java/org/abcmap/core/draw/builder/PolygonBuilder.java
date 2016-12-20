package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.layers.FeatureLayer;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Tool to polygons
 */
public class PolygonBuilder extends AbstractLineBuilder {

    public PolygonBuilder(FeatureLayer layer) {
        super(layer);
    }

    @Override
    protected Geometry getGeometry() {

        // polygon must have at least 4 summit
        // when 2 or 3 summits are available, draw a linestring
        if (shapePoints.size() < 4) {
            return geometryFactory.createLineString(shapePoints.toArray(new Coordinate[shapePoints.size()]));
        }

        // after draw a polygon
        // first and last point of list have to be the same
        Coordinate first = shapePoints.get(0);
        Coordinate last = shapePoints.get(shapePoints.size() - 1);

        // construct the shape with a copy
        ArrayList<Coordinate> sc2 = new ArrayList<>();
        sc2.addAll(shapePoints);

        // if first and last are not the same, add the first point to the list
        if (first.equals(last) == false) {
            sc2.add(first);
        }

        return geometryFactory.createPolygon(sc2.toArray(new Coordinate[sc2.size()]));
    }

    /**
     * Terminate the current line and reset tool
     *
     * @param endPoint
     */
    @Override
    public SimpleFeature terminateLine(Coordinate endPoint) {

        throwIfNotDrawing();

        // too less points, cancel drawing
        if (shapePoints.size() < 4) {
            cancelDrawing();
            return null;
        }

        return super.terminateLine(endPoint);
    }

}
