package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.draw.AbmGeometryType;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.styles.StyleContainer;

/**
 * Tool to draw lines
 */
public class LineBuilder extends AbstractLineBuilder {

    public LineBuilder(AbmFeatureLayer layer, StyleContainer style) {
        super(layer, style);

        // remove background color from style
        if (style.getBackground() != null) {
            this.style = drawm.getStyle(AbmGeometryType.LINE, style.getForeground(), null, style.getThick());
        }
    }

    @Override
    protected Geometry getGeometry() {
        return geometryFactory.createLineString(shapePoints.toArray(new Coordinate[shapePoints.size()]));
    }
}
