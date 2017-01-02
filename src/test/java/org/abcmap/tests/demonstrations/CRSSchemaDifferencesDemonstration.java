package org.abcmap.tests.demonstrations;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by remipassmoilesel on 02/01/17.
 */
public class CRSSchemaDifferencesDemonstration {

    public static void main(String[] args) throws IOException {

        // open h2 database
        Path database = Paths.get("tmp/crsDemonstration.db");
        JDBCDataStore datastore = SQLUtils.getGeotoolsDatastoreFromH2(database);

        // original crs used
        DefaultGeographicCRS originalCrs = DefaultGeographicCRS.WGS84;

        // create a feature type
        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName("feature1");
        tbuilder.setCRS(originalCrs);
        tbuilder.add("geometry", Geometry.class);
        SimpleFeatureType type = tbuilder.buildFeatureType();

        // create a schema
        datastore.createSchema(type);
        CoordinateReferenceSystem schemaCrs = datastore.getFeatureSource(type.getTypeName()).getSchema().getCoordinateReferenceSystem();

        // compare CRS
        System.out.println("originalCrs.equals(schemaCrs)");
        System.out.println(originalCrs.equals(schemaCrs));

        System.out.println();
        System.out.println("originalCrs");
        System.out.println(originalCrs);

        System.out.println();
        System.out.println("schemaCrs");
        System.out.println(schemaCrs);
    }

}
