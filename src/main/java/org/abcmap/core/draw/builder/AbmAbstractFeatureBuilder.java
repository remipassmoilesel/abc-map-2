package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.core.utils.GeoUtils;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Abstract class that allow to build shape and add it to a specified layer
 * <p>
 * Shape builders are temporary objects, associated with one layer and current project.
 * Each time you to draw something, you should create a new draw builder.
 * <p>
 * Shapes are inserted in layer at end of drawing, if drawing is confirmed.
 * <p>
 * Temporary shapes (before confirmation) can be displayed with graphics by using draw() method
 */
public abstract class AbmAbstractFeatureBuilder {

    protected final DrawManager drawm;

    /**
     * Feature builder associated with layer
     */
    protected AbmSimpleFeatureBuilder featureBuilder;

    /**
     * Utility used to build geometries
     */
    protected final GeometryFactory geometryFactory = GeoUtils.getGeometryFactory();

    /**
     * Project whee shape is drawn
     */
    protected final Project project;

    /**
     * Layer where shape is drawn
     */
    protected final AbmFeatureLayer layer;

    /**
     * Style container for current shape. Can be null.
     */
    protected StyleContainer style;

    /**
     * Current feature containing shep being drawn
     */
    protected SimpleFeature currentFeature;


    /**
     * Construct a shape builder associated with a layer and an optional affine transform.
     * <p>
     *
     * @param layer
     */
    public AbmAbstractFeatureBuilder(AbmFeatureLayer layer, StyleContainer style) {

        this.drawm = Main.getDrawManager();
        this.project = Main.getProjectManager().getProject();
        this.style = style;
        this.layer = layer;
        this.featureBuilder = layer.getFeatureBuilder();

    }

    /**
     * Cancel the current drawing operation. This method can have no effect.
     */
    public abstract void cancelDrawing();

    /**
     * Confirm drawing by adding current feature to layer.
     * <p>
     * Return a simple feature, which should be almost the same as the current one,
     * with a possible different ID (this depend on storage)
     * <p>
     * By default write the shape in layer
     */
    public SimpleFeature confirmDrawing() {
        return layer.addFeature(currentFeature);
    }

    /**
     * Return true if this tool is currently drawing
     *
     * @return
     */
    public boolean isDrawing() {
        return currentFeature != null;
    }

    /**
     * Build a default feature with specified geometry
     *
     * @param geom
     * @return
     */
    protected SimpleFeature buildFeature(Geometry geom) {
        return featureBuilder.build(geom);
    }

    /**
     * Apply style to the current feature.
     */
    protected void applyStyle() {

        if (style == null) {
            throw new NullPointerException("Style is null");
        }

        project.getStyleLibrary().applyStyle(style, layer, currentFeature);
    }

    /**
     * Return current style
     *
     * @return
     */
    public StyleContainer getStyle() {
        return style;
    }

    /**
     * Throw an exception if this tool is not currently drawing
     */
    protected void throwIfNotDrawing() {
        if (isDrawing() == false) {
            throw new BuilderException("Cannot perform this operation while not drawing");
        }
    }

    /**
     * Throw an exception if this tool is not currently drawing
     */
    protected void throwIfDrawing() {
        if (isDrawing() == true) {
            throw new BuilderException("Cannot perform this operation while drawing");
        }
    }

    /**
     * Return the active feature layer where tool is supposed to draw
     *
     * @return
     */
    protected AbmFeatureLayer getCurrentLayer() {
        return layer;
    }

    /**
     * Draw current shape, before it is added to a layer
     * <p>
     * Affine transform parameter should be a world to screen one
     *
     * @param g2d
     */
    public void drawCurrentShape(Graphics2D g2d, AffineTransform transform) {

        // utility for conversion
        ShapeWriter shapeWriter = new ShapeWriter(new AffinePointTransformation(transform));

        // convert shape
        Shape shape = shapeWriter.toShape((Geometry) currentFeature.getDefaultGeometry());

        // set style
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(5));

        // draw shape
        g2d.draw(shape);

    }

    /**
     * Get current drawn feature or null
     *
     * @return
     */
    public SimpleFeature getCurrentFeature() {
        return currentFeature;
    }
}
