package org.abcmap.core.project;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.CRSUtils;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.util.HashSet;

/**
 * This object is just a wrapper of Geotools layer
 */
public class Layer {

    private static final CustomLogger logger = LogManager.getLogger(Layer.class);
    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory ff = FeatureUtils.getFilterFactory();

    private CoordinateReferenceSystem crs;
    private SimpleFeatureBuilder featureBuilder;
    private SimpleFeatureSource featureSource;
    private FeatureLayer geotoolsLayer;
    private Style style;
    private LayerIndexEntry indexEntry;
    private SimpleFeatureStore featureStore;

    /**
     * Main constructor of a layer. This constructor is protected to avoid creation outside of
     * this package.
     * <p>
     * To create a new layer, use Project.addNewLayer()
     *
     * @param name
     * @param visible
     * @param zindex
     * @param type
     * @param source
     */
    protected Layer(String layerid, String name, boolean visible, int zindex, LayerType type, Object source) {
        this(new LayerIndexEntry(layerid, name, true, zindex, type), source);
    }

    /**
     * Main constructor of a layer. This constructor is protected to avoid creation outside of
     * this package.
     * <p>
     * To create a new layer, use Project.addNewLayer()
     *
     * @param entry
     * @param source optionnal, only needed if this is a read only feature layer
     */
    protected Layer(LayerIndexEntry entry, Object source) {

        this.indexEntry = entry;
        this.style = sf.createStyle();
        this.crs = CRSUtils.GENERIC_2D;

        // create a feature builder associated with the layer
        this.featureBuilder = FeatureUtils.getSimpleFeatureBuilder(entry.getLayerId(), crs);

        if (LayerType.FEATURES.equals(entry.getType())) {

            if (source == null) {
                throw new NullPointerException("Feature source cannot be null");
            }

            this.featureSource = (SimpleFeatureSource) source;
            this.featureStore = (SimpleFeatureStore) featureSource;
            this.geotoolsLayer = new FeatureLayer(featureSource, style);

        } else {
            throw new IllegalArgumentException("Unknown type: " + entry.getType());
        }


    }

    /**
     * Wrap and add a geometry to this layer
     * <p>
     * Return the feature added or null if there was a problem while adding feature
     *
     * @param geom
     */
    public synchronized SimpleFeature addShape(Geometry geom) {

        featureBuilder.add(geom);
        SimpleFeature feature = featureBuilder.buildFeature(null);

        try {
            featureStore.addFeatures(FeatureUtils.asList(feature));
            return feature;
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

    }

    protected FeatureLayer getGeotoolsLayer() {
        return geotoolsLayer;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public void setIndexEntry(LayerIndexEntry indexEntry) {
        this.indexEntry = indexEntry;
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    public LayerType getType() {
        return indexEntry.getType();
    }

    public String getId() {
        return indexEntry.getLayerId();
    }

    public LayerIndexEntry getIndexEntry() {
        return indexEntry;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    /**
     * Return calculated bounds of layer, or null if an IO error occur
     *
     * @return
     */
    public ReferencedEnvelope getBounds() {
        try {
            return featureSource.getFeatures().getBounds();
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Layer layer = (Layer) o;

        return indexEntry != null ? indexEntry.equals(layer.indexEntry) : layer.indexEntry == null;

    }

    @Override
    public int hashCode() {
        return indexEntry != null ? indexEntry.hashCode() : 0;
    }

    public boolean removeFeature(SimpleFeature... features) {

        // create a set
        HashSet<Identifier> ids = new HashSet<>();
        for (SimpleFeature feature : features) {
            ids.add(feature.getIdentifier());
        }

        // try to remove
        try {
            featureStore.removeFeatures(ff.id(ids));
            return true;
        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

}
