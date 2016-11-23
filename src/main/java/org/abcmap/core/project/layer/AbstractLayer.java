package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This object is a wrapper of Geotools layer
 */
public abstract class AbstractLayer implements Comparable<AbstractLayer> {

    protected static final CustomLogger logger = LogManager.getLogger(AbstractLayer.class);
    protected final static StyleFactory sf = FeatureUtils.getStyleFactory();
    protected final static FilterFactory ff = FeatureUtils.getFilterFactory();
    protected final static GeometryFactory geom = GeoUtils.getGeometryFactory();
    protected ProjectManager pman;

    protected final String crsCode;
    protected CoordinateReferenceSystem crs;
    protected org.geotools.map.Layer internalLayer;
    protected LayerIndexEntry indexEntry;
    protected Style layerStyle;


    /**
     * Main constructor of a layer. Layers have to be created with Project.addNewFeatureLayer() instead of this constructor.
     *
     * @param entry
     */
    public AbstractLayer(LayerIndexEntry entry) {
        pman = MainManager.getProjectManager();
        this.indexEntry = entry;
        this.layerStyle = sf.createStyle();
        this.crsCode = "EPSG:404000";
        this.crs = GeoUtils.GENERIC_2D;

        this.layerStyle = sf.createStyle();
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
    public Style getLayerStyle() {
        return layerStyle;
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

    public int getZindex() {
        return indexEntry.getZindex();
    }

    @Override
    public int compareTo(AbstractLayer other) {

        // other layer have a smaller zindex, it have to be under this
        if (other.getZindex() < this.getZindex()) {
            return 1;
        }

        // other layer have a greater zindex, it have to be above this
        else if (other.getZindex() != this.getZindex()) {
            return -1;
        }

        // other layer have the same zindex
        else {
            return 0;
        }

    }


}
