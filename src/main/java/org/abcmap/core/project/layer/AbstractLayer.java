package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.abcmap.core.utils.CRSUtils;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This object is a wrapper of Geotools layer
 */
public abstract class AbstractLayer {

    protected static final CustomLogger logger = LogManager.getLogger(AbstractLayer.class);
    protected final static StyleFactory sf = FeatureUtils.getStyleFactory();
    protected final static FilterFactory ff = FeatureUtils.getFilterFactory();

    protected CoordinateReferenceSystem crs;
    protected org.geotools.map.Layer internalLayer;
    protected LayerIndexEntry indexEntry;
    protected Style style;


    /**
     * Main constructor of a layer. Layers have to be created with Project.addNewFeatureLayer() instead of this constructor.
     *
     * @param entry
     */
    public AbstractLayer(LayerIndexEntry entry) {
        this.indexEntry = entry;
        this.style = sf.createStyle();
        this.crs = CRSUtils.GENERIC_2D;
    }

    /**
     * Return the actual bounds of the layer
     *
     * @return
     */
    public abstract ReferencedEnvelope getBounds();

    /**
     * Return the internal representation of layer
     *
     * @return
     */
    public org.geotools.map.Layer getInternalLayer() {
        return internalLayer;
    }

    /**
     * Return the style associated with the layer
     *
     * @return
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Return the layer type (constant format) of the layer
     *
     * @return
     */
    public LayerType getType() {
        return indexEntry.getType();
    }

    /**
     * Return the unique id of the layer
     *
     * @return
     */
    public String getId() {
        return indexEntry.getLayerId();
    }

    /**
     * Return the index entry of the layer
     *
     * @return
     */
    public LayerIndexEntry getIndexEntry() {
        return indexEntry;
    }

    /**
     * Get coordinate reference system of the layer
     *
     * @return
     */
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    /**
     * Set coordinate reference system of the layer
     *
     * @return
     */
    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractLayer layer = (AbstractLayer) o;

        return indexEntry != null ? indexEntry.equals(layer.indexEntry) : layer.indexEntry == null;

    }

    @Override
    public int hashCode() {
        return indexEntry != null ? indexEntry.hashCode() : 0;
    }

}
