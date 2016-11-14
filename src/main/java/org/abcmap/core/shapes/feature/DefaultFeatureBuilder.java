package org.abcmap.core.shapes.feature;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default feature builder, build features created by Abc-Map.
 * <p>
 * Features building is synchronized.
 */
public class DefaultFeatureBuilder {

    public static final String GEOMETRY_ATTRIBUTE_NAME = "geometry";
//    private static final String STYLE_ID_ATTRIBUTE_NAME = "style_id";

    private final SimpleFeatureBuilder builder;

    public DefaultFeatureBuilder(String featureName, CoordinateReferenceSystem crs) {
        builder = new SimpleFeatureBuilder(getDefaultFeatureType(featureName, crs));
    }

    public SimpleFeature build(Geometry geom) {
        builder.add(geom);
        return builder.buildFeature(null);
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

        return tbuilder.buildFeatureType();
    }

}
