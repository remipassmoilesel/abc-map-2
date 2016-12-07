package org.abcmap.core.partials;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import org.abcmap.core.utils.BufferedImagePersister;
import org.abcmap.gui.components.map.CachedMapPane;
import org.abcmap.gui.components.map.CachedMapPaneMouseController;
import org.apache.commons.io.FileUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.opengis.referencing.FactoryException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class CachedRenderLab {

    public static final Path CACHE_DATABASE_DIR = Paths.get("data/renderedPartialsStore/");
    private static boolean setupWms = false;
    private static boolean setupShape = true;
    private static boolean showStats = true;

    public static void main(String[] args) throws IOException, ServiceException, FactoryException, SQLException {

        //sqlLab();

        FileUtils.deleteDirectory(CACHE_DATABASE_DIR.toFile());

        DataPersisterManager.registerDataPersisters(BufferedImagePersister.getSingleton());

        String shapePath = "data/france-communes/communes-20160119.shp";
        String wmsUrl = "http://ows.terrestris.de/osm/service?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities";
        int wmsLayerIndex = 0;

        MapContent mapContent = new MapContent();

        if (setupWms == true) {

            // retrieve and add wms layer
            WebMapServer wms = new WebMapServer(new URL(wmsUrl));

            WMSCapabilities capabilities = wms.getCapabilities();
            Layer[] namedLayers = WMSUtils.getNamedLayers(capabilities);

            WMSLayer wmsLayer = new WMSLayer(wms, namedLayers[wmsLayerIndex]);

            mapContent.addLayer(wmsLayer);

        }

        if (setupShape == true) {

            // retrieve a shape file and add it to a mapcontent
            Path shape = Paths.get(shapePath);

            FileDataStore dataStore = FileDataStoreFinder.getDataStore(shape.toFile());
            SimpleFeatureSource shapeFileSource = dataStore
                    .getFeatureSource();

            FeatureLayer shapeLayer = new FeatureLayer(shapeFileSource, SLD.createLineStyle(Color.blue, 0.2f));

            mapContent.addLayer(shapeLayer);
        }

        //ReferencedEnvelope start = new ReferencedEnvelope(-2.38d, 0.44d, 48.45d, 49.98d, DefaultGeographicCRS.WGS84);
        //ReferencedEnvelope start = new ReferencedEnvelope(2.38d, 5.44d, 48.45d, 49.98d, DefaultGeographicCRS.WGS84);
        Point2D.Double start = new Point2D.Double(-2.38d, 48.45d);

        //RenderedPartialFactory partMan = new RenderedPartialFactory(mapContent);
        //partMan.intersect(start);

        SwingUtilities.invokeLater(() -> {

            Path p = Paths.get("tmp/database.db");
            RenderedPartialStore store;
            try {
                store = new RenderedPartialStore(p);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to create store");
            }

            CachedMapPane pane = new CachedMapPane(store, mapContent);

            //pane.setWorldBounds(start);
            pane.setWorldPosition(start);

            JFrame frame = new JFrame();
            frame.setContentPane(pane);
            frame.setSize(new Dimension(800, 600));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            frame.setVisible(true);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // listen map move
            CachedMapPaneMouseController mcontrol = new CachedMapPaneMouseController(pane);
            pane.addMouseMotionListener(mcontrol);
            pane.addMouseListener(mcontrol);
            pane.addMouseWheelListener(mcontrol);

            pane.initializeMap();

        });

        if (showStats) {

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Rendered / added in db / loaded from db / loaded from memory / waiting for processing: "
                            + PartialRenderingQueue.getRenderedPartials()
                            + " / " + RenderedPartialStore.getAddedInDatabase()
                            + " / " + PartialRenderingQueue.getLoadedFromDatabase()
                            + " / " + RenderedPartialFactory.getLoadedPartialsReused()
                            + " / " + PartialRenderingQueue.getWaitingPartialsNumber()
                    );
                }
            }, 1000, 1000);

        }

    }

    public static void sqlLab() throws SQLException {
        String PRECISION = "0.000001";

        JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:h2:./" + CachedRenderLab.CACHE_DATABASE_DIR.resolve("partials.db") + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE", "", "");
        connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        connectionSource.setTestBeforeGet(true);
        connectionSource.initialize();

        // create tables
        TableUtils.createTableIfNotExists(connectionSource, SerializableRenderedPartial.class);

        //-1.6999999999999995	0.0	49.300000000000004	51.0
        ReferencedEnvelope area = new ReferencedEnvelope(-1.699999999d, 0.0d, 49.3d, 51.0d, DefaultGeographicCRS.WGS84);

        // create dao object
        Dao<SerializableRenderedPartial, ?> dao = DaoManager.createDao(connectionSource, SerializableRenderedPartial.class);

        Where<SerializableRenderedPartial, ?> statement = dao.queryBuilder().where().raw(
                "ABS(" + SerializableRenderedPartial.PARTIAL_X1_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_X2_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_Y1_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_Y2_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND CRS=?;",

                new SelectArg(SqlType.DOUBLE, area.getMinX()),
                new SelectArg(SqlType.DOUBLE, area.getMaxX()),
                new SelectArg(SqlType.DOUBLE, area.getMinY()),
                new SelectArg(SqlType.DOUBLE, area.getMaxY()),
                //new SelectArg(SqlType.STRING, SerializableRenderedPartial.crsToId(area.getCoordinateReferenceSystem())))
                new SelectArg(SqlType.STRING, "null:WGS84(DD)"));
//
//        Where<SerializableRenderedPartial, ?> statement = dao.queryBuilder().where().raw(
//                "ABS(" + SerializableRenderedPartial.PARTIAL_X1_FIELD_NAME + " - ?) < " + PRECISION + " "
//                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_X2_FIELD_NAME + " - ?) < " + PRECISION + " ",
//
//                new SelectArg(SqlType.DOUBLE, area.getMinX()),
//                new SelectArg(SqlType.DOUBLE, area.getMaxX())
//        );

        System.out.println(statement);

        List<SerializableRenderedPartial> results = statement.query();

        System.out.println(results);

        System.exit(0);

    }

}
