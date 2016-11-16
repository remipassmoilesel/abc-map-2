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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This object is just a wrapper of Geotools layer
 */
public class Layer {

    private static final CustomLogger logger = LogManager.getLogger(Layer.class);
    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory ff = FeatureUtils.getFilterFactory();

    private CoordinateReferenceSystem crs;
    private DefaultFeatureBuilder featureBuilder;
    private SimpleFeatureSource featureSource;
    private org.geotools.map.Layer internalLayer;
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
    public Layer(String layerid, String name, boolean visible, int zindex, LayerType type, Object source) {
        this(new LayerIndexEntry(layerid, name, visible, zindex, type), source);
    }

    /**
     * Main constructor of a layer. Layers have to be created with Project.addNewLayer() instead of this constructor.
     *
     * @param entry
     * @param source optionnal, only needed if this is a read only feature layer
     */
    public Layer(LayerIndexEntry entry, Object source) {

        this.indexEntry = entry;
        this.style = sf.createStyle();
        this.crs = CRSUtils.GENERIC_2D;

        // create a feature builder associated with the layer
        this.featureBuilder = FeatureUtils.getDefaultFeatureBuilder(entry.getLayerId(), crs);

        if (LayerType.FEATURES.equals(entry.getType())) {

            if (source == null) {
                throw new NullPointerException("Feature source cannot be null");
            }

            this.featureSource = (SimpleFeatureSource) source;
            this.featureStore = (SimpleFeatureStore) featureSource;
            this.internalLayer = new FeatureLayer(featureSource, style);

        } else {
            throw new IllegalArgumentException("Unknown type: " + entry.getType());
        }


    }

    /**
     * Wrap a geometry in a DefaultSimpleFeature and add it to this layer
     * <p>
     * Return the feature added
     * <p>
     * TODO: find a way to remove synchronize attribute ?
     *
     * @param geom
     */
    public SimpleFeature addShape(Geometry geom) {
        SimpleFeature feature = featureBuilder.build(geom);
        return addFeature(feature);
    }

    /**
     * Add a feature to the layer. Can be used to write modifications on feature.
     *
     * @param feature
     * @return
     */
    public SimpleFeature addFeature(SimpleFeature feature) {

        try {
            featureStore.addFeatures(FeatureUtils.asList(feature));
            return feature;
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }

    }

    /**
     * Update specified feature
     *
     * @param feature
     */
    public SimpleFeature updateFeature(SimpleFeature feature) {

        try {
            HashMap<Name, Object> attributes = FeatureUtils.getAttributes(feature);
            Set<Name> keyset = attributes.keySet();
            Name[] names = keyset.toArray(new Name[keyset.size()]);
            Object[] values = attributes.values().toArray();

            featureStore.modifyFeatures(names, values, FeatureUtils.getIdFilter(feature.getID()));
            return feature;
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    /**
     * Return the internal representation of layer
     *
     * @return
     */
    public org.geotools.map.Layer getInternalLayer() {
        return internalLayer;
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
     * Return calculated bounds of layer
     *
     * @return
     */
    public ReferencedEnvelope getBounds() {
        try {
            return featureSource.getFeatures().getBounds();
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
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

    /**
     * Remove features from layer
     *
     * @param features
     */
    public void removeFeatures(SimpleFeature... features) {

        // create a set
        HashSet<Identifier> ids = new HashSet<>();
        for (SimpleFeature feature : features) {
            ids.add(feature.getIdentifier());
        }

        // try to remove
        try {
            featureStore.removeFeatures(ff.id(ids));
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    /**
     * Execute a visit on this layer.
     *
     * @param visitor
     */
    public void executeVisit(LayerVisitor visitor) {
        LayerVisitExecutor executor = new LayerVisitExecutor(this);
        executor.execute(visitor, null);
    }

    /**
     * Execute a visit on this layer. Filter can be null.
     *
     * @param visitor
     * @param filter
     */
    public void executeVisit(LayerVisitor visitor, Filter filter) {
        LayerVisitExecutor executor = new LayerVisitExecutor(this);
        executor.execute(visitor, filter);
    }

    /**
     * Return selected features or null if nothing found
     */
    public SimpleFeature getFeatureById(String id) {

        Filter filter = FeatureUtils.getIdFilter(id);

        try {
            SimpleFeatureIterator features = featureSource.getFeatures(filter).features();

            if (features.hasNext() == false) {
                return null;
            } else {

                SimpleFeature feat = features.next();

                while (features.hasNext()) {
                    logger.warning("Double id value found: " + features.next());
                }

                features.close();

                return feat;
            }

        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }


}
