package org.abcmap.core.rendering.partials;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Special feature representing a partial
 */
public class PartialOutlineFeatureBuilder {

    private static final FilterFactory2 ff = FeatureUtils.getFilterFactory();

    /**
     * This name will be used as a table name
     */
    public static final String FEATURE_NAME = ConfigurationConstants.SQL_TABLE_PREFIX + "PARTIAL_OUTLINES";

    /**
     * Name of geometry attribute of feature
     */
    public static final String GEOMETRY_ATTRIBUTE_NAME = "GEOMETRY";

    /**
     * Corresponding partial id stored in feture
     */
    public static final String PARTIAL_ID_ATTRIBUTE_NAME = "PARTIAL_ID";

    /**
     * Corresponding layer id
     */
    public static final String LAYER_ID_ATTRIBUTE_NAME = "LAYER_ID";

    private final SimpleFeatureBuilder builder;

    public PartialOutlineFeatureBuilder(CoordinateReferenceSystem system) {
        builder = new SimpleFeatureBuilder(getPartialFeatureType(system));
    }

    public synchronized SimpleFeature build(Geometry geom, Long partialId, String layerId) {
        builder.add(geom);
        builder.add(partialId);
        builder.add(layerId);
        return builder.buildFeature(null);
    }

    public static SimpleFeatureType getPartialFeatureType(CoordinateReferenceSystem system) {

        // create a feature type
        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName(FEATURE_NAME);
        tbuilder.setCRS(system);
        tbuilder.add(GEOMETRY_ATTRIBUTE_NAME, Geometry.class);
        tbuilder.add(PARTIAL_ID_ATTRIBUTE_NAME, Long.class);
        tbuilder.add(LAYER_ID_ATTRIBUTE_NAME, String.class);

        return tbuilder.buildFeatureType();
    }

    /**
     * Return current type
     *
     * @return
     */
    public SimpleFeatureType getType() {
        return builder.getFeatureType();
    }

    /**
     * Return id from a partial feature
     *
     * @param feat
     * @return
     */
    public static Long getId(Feature feat) {
        return (Long) feat.getProperty(PartialOutlineFeatureBuilder.PARTIAL_ID_ATTRIBUTE_NAME).getValue();
    }

    /**
     * Get a filter using specified layer id. All other IDs will be rejected
     *
     * @param layerId
     * @return
     */
    public static Filter getLayerIdFilter(String layerId) {
        return ff.equal(ff.property(LAYER_ID_ATTRIBUTE_NAME), ff.literal(layerId), true);
    }

    /**
     * Get a filter using specified area. All features that do not intersect this area will be rejected
     *
     * @param env
     * @return
     */
    public static Filter getAreaFilter(ReferencedEnvelope env) {
        return ff.bbox(ff.property(PartialOutlineFeatureBuilder.GEOMETRY_ATTRIBUTE_NAME), env);
    }


}
