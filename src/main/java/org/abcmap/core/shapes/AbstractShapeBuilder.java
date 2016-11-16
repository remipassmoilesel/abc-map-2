package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeomUtils;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Abstract class that represent a shape builder.
 * <p>
 * Shape builders are temporary objects, associated with current project.
 * <p>
 * // TODO: check if always on the same layer ?
 */
public abstract class AbstractShapeBuilder {

    protected GeometryFactory geometryFactory = GeomUtils.getGeometryFactory();
    protected final Project project;

    /**
     * Style container for current shape. Can be null.
     */
    protected StyleContainer style;

    /**
     * Current feature containing being drawn
     */
    protected SimpleFeature currentFeature;

    public AbstractShapeBuilder() {
        this.project = MainManager.getProjectManager().getProject();
        this.style = null;
    }

    /**
     * Cancel the current drawing operation. This method can have no effect.
     */
    public abstract void cancelDrawing();

    /**
     * Return true if this tool is currently drawing
     *
     * @return
     */
    public boolean isDrawing() {
        return currentFeature != null;
    }

    /**
     * Apply style to the current feature. Throw  NullPointerEx if current feature is null
     */
    protected void applyStyle() {

        if (currentFeature == null) {
            throw new NullPointerException("Current feature is null");
        }

        if (style != null) {
            FeatureUtils.applyStyle(style, currentFeature);
            currentFeature = project.getActiveLayer().updateFeature(currentFeature);
        }
    }

    public StyleContainer getStyle() {
        return style;
    }

    public void setStyle(StyleContainer style) {
        this.style = style;
    }

    /**
     * Throw an exception if this tool is not currently drawing
     */
    protected void throwIfNotDrawing() {
        if (isDrawing() == false) {
            throw new DrawingException("Cannot perform this operation while not drawing");
        }
    }

    /**
     * Throw an exception if this tool is not currently drawing
     */
    protected void throwIfDrawing() {
        if (isDrawing() == true) {
            throw new DrawingException("Cannot perform this operation while drawing");
        }
    }

}
