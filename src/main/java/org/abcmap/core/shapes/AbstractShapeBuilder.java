package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.FeatureLayer;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Abstract class that represent a shape builder.
 * <p>
 * Shape builders are temporary objects, associated with current project.
 * <p>
 * // TODO: check if always on the same layer ?
 */
public abstract class AbstractShapeBuilder {

    protected final FeatureLayer activeLayer;
    protected GeometryFactory geometryFactory = GeoUtils.getGeometryFactory();
    protected final Project project;

    /**
     * Style container for current shape. Can be null.
     */
    protected StyleContainer style;

    /**
     * Current feature containing being drawn
     */
    protected SimpleFeature currentFeature;

    public AbstractShapeBuilder(FeatureLayer layer)  {

        this.project = MainManager.getProjectManager().getProject();
        this.style = null;
        this.activeLayer = (FeatureLayer) project.getActiveLayer();
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
        if (style != null) {
            project.getStyleLibrary().applyStyle(style, activeLayer, currentFeature);
            currentFeature = getActiveLayer().updateFeature(currentFeature);
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
            throw new ShapeBuilderException("Cannot perform this operation while not drawing");
        }
    }

    /**
     * Throw an exception if this tool is not currently drawing
     */
    protected void throwIfDrawing() {
        if (isDrawing() == true) {
            throw new ShapeBuilderException("Cannot perform this operation while drawing");
        }
    }

    /**
     * Return the active feature layer where tool is supposed to draw
     *
     * @return
     */
    protected FeatureLayer getActiveLayer() {
        return activeLayer;
    }
}
