package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.draw.DefaultFeatureBuilder;
import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.project.layers.FeatureLayer;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Draw line on map with current style
 */
public class LineTool extends MapTool {

    private final BasicStroke indicationStroke;
    private LineBuilder builder;
    private Point mousePosition;

    public LineTool() {
        super();
        this.indicationStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        mousePosition = e.getPoint();
        repaintMainMap();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        mousePosition = null;
        repaintMainMap();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        FeatureLayer layer = getActiveFeatureLayerIfLeftClickOrShowMessage(e);
        if (layer == null) {
            return;
        }

        // one click only
        if (e.getClickCount() < 2) {

            // first point of line
            if (builder == null) {
                builder = drawm.getLineBuilder();
                builder.newLine(screenToWorldCoordinate(e.getPoint()));
            }

            // other points of line
            else {
                builder.addPoint(screenToWorldCoordinate(e.getPoint()));
            }

        }

        // double click or more
        else if (e.getClickCount() > 1) {

            if (builder != null) {
                SimpleFeature feat = builder.terminateLine(screenToWorldCoordinate(e.getPoint()));
                builder = null;

                ReferencedEnvelope bounds = JTS.bounds(DefaultFeatureBuilder.getGeometry(feat), projectm.getProject().getCrs());

                deleteActiveLayerCache(bounds);
            }

        }

        repaintMainMap();

    }

    @Override
    public void drawOnMainMap(Graphics2D g2d) {
        super.drawOnMainMap(g2d);

        // draw current shape if necessary
        if (builder != null) {

            // only use a copy of graphics
            Graphics2D g2dc = (Graphics2D) g2d.create();

            AffineTransform wts = getMainMapWorldToScreen();
            builder.drawCurrentShape(g2dc, wts);

            // draw indication on drawing if needed
            ArrayList<Coordinate> points = builder.getPoints();
            if (points.size() > 0 && mousePosition != null) {
                Coordinate fromCoord = points.get(points.size() - 1);
                Point2D fromPoint = wts.transform(new Point2D.Double(fromCoord.x, fromCoord.y), null);

                g2dc.setColor(Color.yellow);
                g2dc.setStroke(indicationStroke);
                g2dc.drawLine((int) fromPoint.getX(), (int) fromPoint.getY(), mousePosition.x, mousePosition.y);
            }

        }
    }


}
