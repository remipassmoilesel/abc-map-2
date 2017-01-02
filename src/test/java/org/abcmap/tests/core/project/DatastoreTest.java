package org.abcmap.tests.core.project;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.SQLUtils;
import org.apache.commons.io.FileUtils;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * Small test about Geopackage and Datastore life cycle
 */
public class DatastoreTest {

    private static final GeometryFactory geom = GeoUtils.getGeometryFactory();

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.softwareInit();
    }

    @Test
    public void tests() throws IOException {

        Path tempDir = TestUtils.PLAYGROUND_DIRECTORY.resolve("datastoreTest");
        FileUtils.deleteDirectory(tempDir.toFile());
        Files.createDirectories(tempDir);

        Path db = tempDir.resolve("project.h2");
        Files.deleteIfExists(db);
        Files.createFile(db);

        JDBCDataStore datastore = SQLUtils.getGeotoolsDatastoreFromH2(db);
        String featureId = "feature1";

        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName(featureId);
        tbuilder.setCRS(DefaultGeographicCRS.WGS84);
        tbuilder.add("geometry", Geometry.class);

        SimpleFeatureType type = tbuilder.buildFeatureType();
        SimpleFeatureBuilder fbuilder = new SimpleFeatureBuilder(type);

        // get feature store in database
        datastore.createSchema(type);

        FeatureStore featureStore = (FeatureStore) datastore.getFeatureSource("feature1");

        for (int i = 0; i < 100; i++) {
            fbuilder.add(geom.createPoint(TestUtils.getRandomPoint()));
            fbuilder.buildFeature(null);

            featureStore.addFeatures(FeatureUtils.asList(fbuilder.buildFeature(null)));
        }

        int count = 0;
        FeatureIterator it = featureStore.getFeatures().features();
        while (it.hasNext()) {
            Feature f = it.next();
            count++;
        }
        it.close();

        assertTrue("Use featurestore after closing geopackage", count == 100);

        // test reading after datastore closing
        datastore.dispose();

        boolean exception = false;
        try {
            featureStore.getFeatures().features();
        } catch (Exception e) {
            exception = true;
        }
        assertTrue("Datastore is unavailable after dispose() call", exception);


    }

}
