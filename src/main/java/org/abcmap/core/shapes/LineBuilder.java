package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.Project;

/**
 * Tool to draw lines
 */
public class LineBuilder extends AbstractPolylineBuilder {

    @Override
    protected Geometry getGeometry() {
        return geometryFactory.createLineString(shapePoints.toArray(new Coordinate[shapePoints.size()]));
    }

}
