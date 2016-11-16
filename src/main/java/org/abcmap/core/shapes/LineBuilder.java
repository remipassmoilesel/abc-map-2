package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.FeatureLayer;

import java.util.ArrayList;

/**
 * Tool to draw lines
 */
public class LineBuilder extends AbstractPolylineBuilder {

    public LineBuilder(FeatureLayer layer) {
        super(layer);
    }


    @Override
    protected Geometry getGeometry() {
        return geometryFactory.createLineString(shapePoints.toArray(new Coordinate[shapePoints.size()]));
    }

}
