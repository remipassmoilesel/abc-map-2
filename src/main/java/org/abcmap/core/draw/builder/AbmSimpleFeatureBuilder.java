package org.abcmap.core.draw.builder;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default feature builder, build features created by Abc-Map.
 * <p>
 * Features building is synchronized.
 */
public class AbmSimpleFeatureBuilder {

    public static final String ABCMAP_SPECIAL_FIELD_PREFIX = "abm_";
    public static final String GEOMETRY_ATTRIBUTE_NAME = ABCMAP_SPECIAL_FIELD_PREFIX + "geometry";
    public static final String STYLE_ID_ATTRIBUTE_NAME = ABCMAP_SPECIAL_FIELD_PREFIX + "style_id";

    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory2 ff = FeatureUtils.getFilterFactory();

    private final SimpleFeatureBuilder builder;
    private final SimpleFeatureType currentFeatureType;

    public AbmSimpleFeatureBuilder(String featureName, CoordinateReferenceSystem crs) {
        currentFeatureType = getDefaultFeatureType(featureName, crs);
        builder = new SimpleFeatureBuilder(currentFeatureType);
    }

    /**
     * Build a feature with associated geometry
     *
     * @param geom
     * @return
     */
    public synchronized SimpleFeature build(Geometry geom) {
        return build(geom, -1l);
    }

    /**
     * Build a feature with associated geometry and style
     *
     * @param geom
     * @return
     */
    public synchronized SimpleFeature build(Geometry geom, Long styleId) {
        builder.add(geom);
        builder.add(styleId);
        return builder.buildFeature(null);
    }

    /**
     * Return current feature type
     *
     * @return
     */
    public SimpleFeatureType getCurrentFeatureType() {
        return currentFeatureType;
    }

    /**
     * Get the default feature type, with geometry, a secondary identifier and a style ID.
     * <p>
     *
     * @param name
     * @return
     */
    public static SimpleFeatureType getDefaultFeatureType(String name, CoordinateReferenceSystem crs) {

        // create a feature type
        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName(name);
        tbuilder.setCRS(crs);
        tbuilder.add(GEOMETRY_ATTRIBUTE_NAME, Geometry.class);
        tbuilder.add(STYLE_ID_ATTRIBUTE_NAME, Long.class);

        return tbuilder.buildFeatureType();
    }

    /**
     * Return a geometry filter associated with default features
     * <p>
     * This filter match when geometries intersect specified bounds
     *
     * @return
     */
    public static Filter getIntersectGeometryFilter(ReferencedEnvelope envelope) {
        return ff.bbox(ff.property(GEOMETRY_ATTRIBUTE_NAME), envelope);
    }

    /**
     * Return a geometry filter associated with default features
     * <p>
     * This filter match when geometries include specified bounds
     *
     * @return
     */
    public static Filter getIncludeGeometryFilter(ReferencedEnvelope envelope) {
        return ff.within(ff.property(GEOMETRY_ATTRIBUTE_NAME), ff.literal(JTS.toGeometry(envelope)));
    }

    /**
     * Return geometry associated with a default feature
     *
     * @param feat
     * @return
     */
    public static Geometry getGeometry(SimpleFeature feat) {

        if (feat == null) {
            throw new NullPointerException("Feature cannot be null");
        }

        return (Geometry) feat.getAttribute(GEOMETRY_ATTRIBUTE_NAME);
    }


    /**
     * Return style id of specified DefaultSimpleFeature
     *
     * @param feature
     * @return
     */
    public static String getStyleId(SimpleFeature feature) {

        if (feature == null) {
            throw new NullPointerException("Feature cannot be null");
        }

        return (String) feature.getAttribute(AbmSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME);
    }


}
