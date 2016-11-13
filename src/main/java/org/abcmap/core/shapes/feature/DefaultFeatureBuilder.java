package org.abcmap.core.shapes.feature;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default feature builder, build features created by Abc-Map.
 * <p>
 * A secondary identifier is used as a workaround for the Geotools JDBC/Geopackage  id issue (see tests)
 */
public class DefaultFeatureBuilder {

    private final SimpleFeatureBuilder builder;

    public DefaultFeatureBuilder(String featureName, CoordinateReferenceSystem crs) {
        builder = new SimpleFeatureBuilder(getDefaultFeatureType(featureName, crs));
    }

    public synchronized SimpleFeature build(Geometry geom, String styleId) {
        builder.add(geom);
        builder.add(FeatureUtils.generateFeatureId());
        builder.add(styleId);

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
        tbuilder.add("geometry", Geometry.class);
        tbuilder.add("secondary_id", String.class);
        tbuilder.add("style_id", String.class);

        return tbuilder.buildFeatureType();
    }

}
