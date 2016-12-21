package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.styles.StyleContainer;
import org.opengis.feature.simple.SimpleFeature;

public class PointBuilder extends AbstractShapeBuilder {

    public PointBuilder(FeatureLayer layer, StyleContainer style) {
        super(layer, style);
    }

    /**
     * Create a new point.
     * <p>
     * Positions are in world coordinates.
     *
     * @param coord
     */
    public SimpleFeature addPoint(Coordinate coord) {
        currentFeature = buildFeature(geometryFactory.createPoint(coord));
        applyStyle();
        return currentFeature;
    }

    @Override
    public void cancelDrawing() {
        // Nothing to do here
    }

}
