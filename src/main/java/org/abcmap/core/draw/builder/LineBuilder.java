package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.layers.FeatureLayer;

import java.awt.geom.AffineTransform;

/**
 * Tool to draw lines
 */
public class LineBuilder extends AbstractLineBuilder {

    public LineBuilder(FeatureLayer layer) {
        super(layer);
    }

    @Override
    protected Geometry getGeometry() {
        return geometryFactory.createLineString(shapePoints.toArray(new Coordinate[shapePoints.size()]));
    }

}
