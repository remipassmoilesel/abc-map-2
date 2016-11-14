package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.project.Project;
import org.opengis.feature.simple.SimpleFeature;

public class PointBuilder extends AbstractShapeBuilder {

    /**
     * Create a new point.
     * <p>
     * Positions are in world coordinates.
     *
     * @param coord
     */
    public SimpleFeature addPoint(Coordinate coord) {
        return project.getActiveLayer().addShape(geometryFactory.createPoint(coord));
    }

    @Override
    public void cancelDrawing() {
        // Nothing to do here
    }

}
