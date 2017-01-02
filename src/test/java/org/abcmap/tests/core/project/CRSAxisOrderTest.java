package org.abcmap.tests.core.project;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;

/**
 * Test if CRS axis are in good order after insertion in database. This is essential to prevent error in display.
 */
public class CRSAxisOrderTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void test() throws IOException, FactoryException {

        // open h2 database
        ProjectManager pman = MainManager.getProjectManager();
        Path database = pman.getProject().getDatabasePath();
        JDBCDataStore datastore = SQLUtils.getGeotoolsDatastoreFromH2(database);

        // create system with longitude axis first
        CoordinateReferenceSystem originalCrs = GeoUtils.decode("EPSG:4326");

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
        assertTrue("CRS basic equality test", originalCrs.equals(originalCrs));
        assertTrue("CRS equality test: " + originalCrs + " //// \n" + schemaCrs, originalCrs.equals(schemaCrs));

    }

}
