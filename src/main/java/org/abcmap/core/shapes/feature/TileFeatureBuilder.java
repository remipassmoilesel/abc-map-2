package org.abcmap.core.shapes.feature;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Special feature representing a tile.
 * <p>
 * This feature store a geometry with the outline of tile, and the id of associated tile
 */
public class TileFeatureBuilder {

    public static final String GEOMETRY_ATTRIBUTE_NAME = "geometry";
    public static final String TILE_ID_ATTRIBUTE_NAME = "tile_id";

    private final SimpleFeatureBuilder builder;

    public TileFeatureBuilder(String featureName, CoordinateReferenceSystem crs) {
        builder = new SimpleFeatureBuilder(getTileFeatureType(featureName, crs));
    }

    public synchronized SimpleFeature build(Geometry geom, String tileId) {
        builder.add(geom);
        builder.add(tileId);
        return builder.buildFeature(null);
    }

    /**
     * Get the default feature type, with geometry, a secondary identifier and a style ID.
     * <p>
     *
     * @param name
     * @return
     */
    public static SimpleFeatureType getTileFeatureType(String name, CoordinateReferenceSystem crs) {

        // create a feature type
        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName(name);
        tbuilder.setCRS(crs);
        tbuilder.add(GEOMETRY_ATTRIBUTE_NAME, Geometry.class);
        tbuilder.add(TILE_ID_ATTRIBUTE_NAME, String.class);

        return tbuilder.buildFeatureType();
    }

}
