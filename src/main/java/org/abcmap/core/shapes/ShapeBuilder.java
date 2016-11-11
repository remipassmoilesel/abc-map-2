package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeomUtils;

/**
 * Abstract class that represent a shape builder.
 */
public abstract class ShapeBuilder {

    protected final Project project;
    protected GeometryFactory geometryFactory = GeomUtils.getGeometryFactory();

    public ShapeBuilder(Project p) {
        this.project = p;
    }
}
