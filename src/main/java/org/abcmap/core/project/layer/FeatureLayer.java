package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.SQLiteUtils;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.Identifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by remipassmoilesel on 16/11/16.
 */
public class FeatureLayer extends AbstractLayer {

    protected SimpleFeatureStore featureStore;
    protected DefaultFeatureBuilder featureBuilder;
    protected SimpleFeatureSource featureSource;

    public FeatureLayer(String layerId, String title, boolean visible, int zindex, GeoPackage geopkg, boolean create) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.FEATURES), geopkg, create);
    }

    public FeatureLayer(LayerIndexEntry entry, GeoPackage geopkg, boolean create) throws IOException {
        super(entry);

        // if true, create a new geopackage entry
        if (create) {
            // create a simple feature type
            // /!\ If DefaultEngineeringCRS.GENERIC2D is used, a lot of time is waste.
            // Prefer CRS.decode("EPSG:40400");
            SimpleFeatureType type = DefaultFeatureBuilder.getDefaultFeatureType(entry.getLayerId(), this.crs);
            FeatureEntry fe = new FeatureEntry();
            fe.setBounds(new ReferencedEnvelope());
            fe.setSrid(null);

            // create a geopackage entry
            geopkg.create(fe, type);
        }

        JDBCDataStore datastore = SQLiteUtils.getDatastoreFromGeopackage(geopkg.getFile().toPath());
        this.featureSource = datastore.getFeatureSource(entry.getLayerId());
        this.featureStore = (SimpleFeatureStore) featureSource;

        // create a feature builder associated with the layer
        this.featureBuilder = FeatureUtils.getDefaultFeatureBuilder(entry.getLayerId(), crs);
        this.internalLayer = new org.geotools.map.FeatureLayer(featureSource, style);

    }

    /**
     * Return calculated bounds of layer
     *
     * @return
     */
    @Override
    public ReferencedEnvelope getBounds() {
        try {
            return featureSource.getFeatures().getBounds();
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
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
