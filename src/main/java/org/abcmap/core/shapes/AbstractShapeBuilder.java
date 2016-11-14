package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeomUtils;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Abstract class that represent a shape builder.
 * <p>
 * Shape builders are temporary objects, associated with current project.
 */
public abstract class AbstractShapeBuilder {

    protected final Project project;
    protected GeometryFactory geometryFactory = GeomUtils.getGeometryFactory();
    protected SimpleFeature currentFeature;

    public AbstractShapeBuilder() {
        this.project = MainManager.getProjectManager().getProject();
    }

    /**
     * Cancel the current drawing operation. This method can have no effect.
     */
    public abstract void cancelDrawing();

    public boolean isDrawing() {
        return currentFeature != null;
    }
}
