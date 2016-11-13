package org.abcmap.core.project.layer;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Allow to visit all features in a layer.
 * <p>
 * This pattern is used for decoupling iterations and to avoid iterator closing errors.
 */
public interface LayerVisitor {

    /**
     * Process a feature.
     * <p>
     * If false is return, the visit is stopped
     *
     * @param feature
     * @return
     */
    public boolean processFeature(SimpleFeature feature);
}
