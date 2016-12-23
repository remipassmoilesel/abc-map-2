package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.styles.StyleContainer;

/**
 * Tool to draw lines
 */
public class LineBuilder extends AbstractLineBuilder {

    public LineBuilder(FeatureLayer layer, StyleContainer style) {
        super(layer, style);

        // remove background color from style
        if (style.getBackground() != null) {
            this.style = drawm.getStyle(style.getForeground(), null, style.getThick());
        }
    }

    @Override
    protected Geometry getGeometry() {
        return geometryFactory.createLineString(shapePoints.toArray(new Coordinate[shapePoints.size()]));
    }
}
