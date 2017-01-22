package org.abcmap.core.project.layers;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.MonitoredMapContent;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;

/**
 * This object is a wrapper of Geotools layer
 */
public abstract class AbmAbstractLayer implements Comparable<AbmAbstractLayer> {

    protected static final CustomLogger logger = LogManager.getLogger(AbmAbstractLayer.class);
    protected final static StyleFactory sf = FeatureUtils.getStyleFactory();
    protected final static FilterFactory ff = FeatureUtils.getFilterFactory();
    protected final static GeometryFactory geom = GeoUtils.getGeometryFactory();
    protected final Project project;

    protected Layer internalLayer;
    protected LayerIndexEntry indexEntry;
    protected Style layerStyle;
    protected String readableType;
    private CoordinateReferenceSystem crs;

    /**
     * If true, we should not try get CRS again, maybe CRS is null
     */
    private boolean crsIsNull;

    /**
     * Main constructor of a layer. Layers have to be created with Project.addNewFeatureLayer() instead of this constructor.
     *
     * @param entry
     */
    public AbmAbstractLayer(Project owner, LayerIndexEntry entry) {
        this.project = owner;
        this.indexEntry = entry;
        this.layerStyle = sf.createStyle();
        this.readableType = "Couche sans nom";
    }

    /**
     * Dispose previous internal layer if needed and create a new one
     */
    protected void buildInternalLayer() {

        // dispose previous layer if needed
        if (this.internalLayer != null) {
            internalLayer.dispose();
        }

        // create internal layer
        try {
            this.internalLayer = buildGeotoolsLayer();
        } catch (IOException e) {
            logger.error(e);
            this.internalLayer = null;
        }
    }

    /**
     * This method should return a valid Geotools layer, used to render map
     *
     * @return
     * @throws IOException
     */
    public abstract Layer buildGeotoolsLayer() throws IOException;

    /**
     * This method should be call every time a modification happen to layer
     * <p>
     * in order to display modifications
     */
    protected void deleteCache(ReferencedEnvelope env) {

        GuiUtils.throwIfOnEDT();

        project.deleteCacheForLayer(indexEntry.getLayerId(), env);
    }

    /**
     * Return the actual bounds of the layer, namely its lower left corner and upper right corner.
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
    public AbmLayerType getType() {
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
     * Create a map content with this layer as a single layer.
     * <p>
     * This kind of map content are used to render each layer at a time
     *
     * @return
     */
    public MapContent buildMapContent() {
        MonitoredMapContent content = new MonitoredMapContent();
        content.addLayer(internalLayer);
        return content;
    }

    /**
     * Set opacity, between 0 and 1
     */
    public float getOpacity() {
        return indexEntry.getOpacity();
    }

    /**
     * Set opacity, between 0 and 1
     *
     * @param opacity
     */
    public void setOpacity(float opacity) {
        indexEntry.setOpacity(opacity);

        // do not delete cache here
        //deleteCache();
    }

    /**
     * Get coordinate reference system of layer.
     * <p>
     * Main CRS is the project CRS, but this CRS can be used to transform envelopes before apply them.
     *
     * @return
     */
    public CoordinateReferenceSystem getCrs() {

        // cache CRS to prevent too much compute
        if (crs == null && crsIsNull == false) {
            crs = getInternalLayer().getFeatureSource().getSchema().getCoordinateReferenceSystem();
            if (crs == null) {
                logger.error(new NullPointerException("Crs is null"));
                crsIsNull = true;
            }
        }
        return crs;
    }

    /**
     * Return zindex of layer. 0 is bottom
     *
     * @return
     */
    public int getZindex() {
        return indexEntry.getZindex();
    }

    /**
     * Set zindex of layer. 0 is bottom
     *
     * @return
     */
    public void setZindex(int zindex) {
        indexEntry.setZindex(zindex);
    }

    /**
     * Compare layers between them, in order to sort it by z-order
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(AbmAbstractLayer other) {

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

    /**
     * Return true if this layer should be painted
     *
     * @return
     */
    public boolean isVisible() {
        return indexEntry.isVisible();
    }

    /**
     * Set to true to paint layer
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        indexEntry.setVisible(visible);
    }

    /**
     * Get readable name of layer
     *
     * @return
     */
    public String getName() {
        return indexEntry.getName();
    }

    /**
     * Set readable name of layer
     *
     * @param name
     */
    public void setName(String name) {
        indexEntry.setName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbmAbstractLayer layer = (AbmAbstractLayer) o;

        return indexEntry != null ? indexEntry.equals(layer.indexEntry) : layer.indexEntry == null;

    }

    @Override
    public int hashCode() {
        return indexEntry != null ? indexEntry.hashCode() : 0;
    }

    /**
     * Set a human readable name of this layer.
     * <p>
     * This name can appear on lists, information panels, ...
     *
     * @param readableType
     */
    protected void setReadableType(String readableType) {
        this.readableType = readableType;
    }

    /**
     * Get the human readable name of this layer.
     * <p>
     * This name can appear on lists, information panels, ...
     */
    public String getReadableType() {
        return readableType;
    }
}
