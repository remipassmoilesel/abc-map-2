package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.awt.PointTransformation;
import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.utils.GeoUtils;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Created by remipassmoilesel on 20/12/16.
 */
public class AffinePointTransformation implements PointTransformation {

    private final AffineTransform transformation;

    public AffinePointTransformation(AffineTransform trans) {
        this.transformation = trans;
    }

    @Override
    public void transform(Coordinate src, Point2D dest) {
        transformation.transform(GeoUtils.coordinateToPoint2D(src), dest);
    }
}
