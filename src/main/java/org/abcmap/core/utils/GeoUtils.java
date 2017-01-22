package org.abcmap.core.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.swing.JMapFrame;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ContrastMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 10/11/16.
 */
public class GeoUtils {
    /**
     * Generic 2D coordinate reference system. Prefer use of this instead of
     * DefaultEngineeringSystem.GENERIC2D because its format is easier to serialize.
     */
    public static CoordinateReferenceSystem GENERIC_2D;
    /**
     * Do not use DefaultGeographicCRS.WGS_84: axis order disturb display
     */
    public static CoordinateReferenceSystem WGS_84;

    /**
     * List of known CRS names. These CRS can npt be easily serialized by authority / code.
     */
    public static final ArrayList<String> knownCrsNames = new ArrayList<>();

    /**
     * List of known CRS. These CRS can npt be easily serialized by authority / code.
     */
    public static final ArrayList<CoordinateReferenceSystem> knownCrs = new ArrayList<>();

    /**
     * Main CRS factory used in software. This factory should be used in every decode operation.
     * <p>
     * /!\ EPSG:40400 do not work with this factory
     */
    private static final CRSAuthorityFactory crsFactory;
    public static final CustomLogger logger = LogManager.getLogger(GeoUtils.class);

    static {

        // initialize main crs factory
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        crsFactory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);


        try {
            GENERIC_2D = CRS.decode("EPSG:404000");
            WGS_84 = decode("EPSG:4326");
        } catch (FactoryException e) {
            logger.error(e);
        }

        // Upper case alphanum only
        knownCrsNames.add("CARTESIAN");
        knownCrs.add(DefaultGeocentricCRS.CARTESIAN);
        knownCrsNames.add("EPSG:4326");
        knownCrs.add(WGS_84);
        knownCrsNames.add("CARTESIAN2D");
        knownCrs.add(DefaultEngineeringCRS.CARTESIAN_2D);
        knownCrsNames.add("GENERIC2D");
        knownCrs.add(DefaultEngineeringCRS.GENERIC_2D);

    }

    private static final FilterFactory2 ff = FeatureUtils.getFilterFactory();
    private static final StyleFactory sf = FeatureUtils.getStyleFactory();

    /**
     * Return a JTS geometry factory
     *
     * @return
     */
    public static GeometryFactory getGeometryFactory() {
        return JTSFactoryFinder.getGeometryFactory();
    }

    /**
     * Return an RGB style associated with a coverage reader
     *
     * @param reader
     * @return
     */
    public static org.geotools.styling.Style getDefaultRGBRasterStyle(AbstractGridCoverage2DReader reader) {
        return getDefaultRGBRasterStyle(reader, null);
    }

    /**
     * Return an RGB style associated with a coverage reader
     *
     * @param reader
     * @param params
     * @return
     */
    public static org.geotools.styling.Style getDefaultRGBRasterStyle(AbstractGridCoverage2DReader reader, GeneralParameterValue[] params) {

        GridCoverage2D cov = null;
        try {
            cov = reader.read(params);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return getDefaultRGBRasterStyle(cov);
    }

    /**
     * Return a RGB style associated with a coverage
     * <p>
     * Throw an exception if
     *
     * @param cov
     * @return
     */
    public static org.geotools.styling.Style getDefaultRGBRasterStyle(GridCoverage2D cov) {

        // We need at least three bands to create an RGB style
        int numBands = cov.getNumSampleDimensions();
        if (numBands < 3) {
            throw new IllegalStateException("Need more bands to make an RGB layerStyle: " + numBands);
        }

        // Get the names of the bands
        String[] sampleDimensionNames = new String[numBands];
        for (int i = 0; i < numBands; i++) {
            GridSampleDimension dim = cov.getSampleDimension(i);
            sampleDimensionNames[i] = dim.getDescription().toString();
        }

        final int RED = 0, GREEN = 1, BLUE = 2;
        int[] channelNum = {-1, -1, -1};
        // We examine the band names looking for "red...", "green...", "blue...".
        // Note that the channel numbers we record are indexed from 1, not 0.
        for (int i = 0; i < numBands; i++) {
            String name = sampleDimensionNames[i].toLowerCase();
            if (name != null) {
                if (name.matches("red.*")) {
                    channelNum[RED] = i + 1;
                } else if (name.matches("green.*")) {
                    channelNum[GREEN] = i + 1;
                } else if (name.matches("blue.*")) {
                    channelNum[BLUE] = i + 1;
                }
            }
        }

        // If we didn't find named bands "red...", "green...", "blue..."
        // we fall back to using the first three bands in order
        if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
            channelNum[RED] = 1;
            channelNum[GREEN] = 2;
            channelNum[BLUE] = 3;
        }

        // Now we create a RasterSymbolizer using the selected channels
        SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NONE);
        for (int i = 0; i < 3; i++) {
            sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
        }
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }

    /**
     * Return default gray scale style for raster elements
     *
     * @param bandNum
     * @return
     */
    public static org.geotools.styling.Style getDefaultGrayScaleRasterStyle(Integer bandNum) {

        // Now we create a RasterSymbolizer using the selected channels
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NONE);
        SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(bandNum), ce);
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);

    }

    /**
     * Return a streaming renderer
     */
    public static StreamingRenderer buildRenderer() {
        return buildRenderer(null);
    }

    /**
     * Return a streaming renderer
     */
    public static StreamingRenderer buildRenderer(RenderListener listener) {

        StreamingRenderer renderer = new StreamingRenderer();

        RenderingHints javaHints = new RenderingHints(GuiUtils.getQualityRenderingHints());
        renderer.setJava2DHints(javaHints);

        if (listener != null) {
            renderer.addRenderListener(listener);
        }

        return renderer;
    }

    /**
     * Transform a coordinate object in a point 2D
     *
     * @param coordinate
     * @return
     */
    public static Point2D coordinateToPoint2D(Coordinate coordinate) {
        return new Point2D.Double(coordinate.x, coordinate.y);
    }

    /**
     * Transform a Point2D object in coordinate
     *
     * @param point
     * @return
     */
    public static Coordinate point2DtoCoordinate(Point2D point) {
        return new Coordinate(point.getX(), point.getY());
    }

    /**
     * Transform a Point2D object in coordinate
     *
     * @param point
     * @return
     */
    public static Coordinate point2DtoCoordinate(Point point) {
        return point2DtoCoordinate((Point2D) point);
    }

    /**
     * Return true if specified map content contain specified layer
     *
     * @param map
     * @param layer
     * @return
     */
    public static boolean isMapContains(MapContent map, Layer layer) {
        for (Layer lay : map.layers()) {
            if (lay.equals(layer)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if two envelopes are similar with delta precision
     *
     * @param env1
     * @param env2
     * @param delta
     * @return
     */
    public static boolean compareEnvelopes(ReferencedEnvelope env1, ReferencedEnvelope env2, double delta) {

        if (env1 == env2) {
            return true;
        }

        double[] val1 = new double[]{
                env1.getMinX(),
                env1.getMinY(),
                env1.getMaxX(),
                env1.getMaxY()
        };

        double[] val2 = new double[]{
                env2.getMinX(),
                env2.getMinY(),
                env2.getMaxX(),
                env2.getMaxY()
        };

        for (int i = 0; i < val1.length; i++) {
            double diff = Math.abs(val1[i] - val2[i]);
            if (diff > delta) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return a unique identifier for CRS that can be used to recreate a CRS with stringToCrs()
     * <p>
     * If specified CRS is null, using DefaultEngineeringCRS.GENERIC_2D;
     * <p>
     * /!\ This operation can take a LOOOONG time !
     *
     * @param crs
     * @return
     */
    public static String crsToString(CoordinateReferenceSystem crs) {
        return crsToString(crs, false);
    }

    /**
     * Return a unique identifier for CRS that can be used to recreate a CRS with stringToCrs()
     * <p>
     * If specified CRS is null, using DefaultEngineeringCRS.GENERIC_2D;
     * <p>
     * /!\ This operation can take a LOOOONG time !
     *
     * @param crs
     * @return
     */
    public static String crsToString(CoordinateReferenceSystem crs, boolean completeButSlowScan) {

        if (crs == null) {
            logger.warning("Crs is null: " + crs);
            crs = DefaultEngineeringCRS.GENERIC_2D;
        }

        // CRS is known, return a generic code
        int index = knownCrs.indexOf(crs);
        if (index > -1) {
            return knownCrsNames.get(index);
        } else {
            try {
                // If second parameter is set to true, search can be very looooow
                // String lookup = CRS.lookupIdentifier(crs, true);
                String lookup = CRS.lookupIdentifier(crs, completeButSlowScan);
                if (lookup != null) {
                    return lookup;
                } else {
                    throw new NullPointerException("Lookup identifier for CRS failed, id is null");
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }

        // unknown situation, warning
        // TODO find a solution, do not store WKT for all entries
        logger.error("Unable to serialize CRS: " + crs.getIdentifiers());
        return crs.toWKT();
    }

    /**
     * Create a CRS from a string created with crsToString()
     *
     * @param crsId
     * @return
     * @throws FactoryException
     */
    public static CoordinateReferenceSystem stringToCrs(String crsId) throws FactoryException {

        crsId = crsId.toUpperCase();

        // check if CRS is known
        int index = knownCrsNames.indexOf(crsId);
        if (index > -1) {
            return knownCrs.get(index);
        }

        // try to decode it
        return CRS.decode(crsId);
    }

    /**
     * Show a geotools layer in window
     *
     * @param title
     * @param layer
     */
    public static void showInDebugWindow(String title, FeatureLayer layer) {
        // create a map content
        MapContent mapContent = new MapContent();
        mapContent.addLayer(layer);

        showInDebugWindow(title, mapContent);
    }

    /**
     * Show a geotools layer in window
     *
     * @param title
     * @param content
     */
    public static void showInDebugWindow(String title, MapContent content) {

        SwingUtilities.invokeLater(() -> {

            // create a map frame and show it
            JMapFrame frame = new JMapFrame(content);
            frame.setTitle(title);
            frame.setSize(800, 600);
            frame.enableStatusBar(true);
            frame.enableTool(JMapFrame.Tool.ZOOM, JMapFrame.Tool.PAN, JMapFrame.Tool.RESET);
            frame.enableToolBar(true);
            frame.enableLayerTable(true);
            frame.setVisible(true);

        });

    }

    /**
     * Main CRS factory used in software. This factory should be used in every CRS decode operation.
     * <p>
     * /!\ EPSG:40400 do not work with this factory
     *
     * @param code
     * @return
     * @throws FactoryException
     */
    public static CoordinateReferenceSystem decode(String code) throws FactoryException {
        return crsFactory.createCoordinateReferenceSystem(code);
    }

    /**
     * Return bounds of a feature collection list
     *
     * @param features
     * @return
     */
    public static ReferencedEnvelope getBoundsFromFeatureList(ArrayList<SimpleFeature> features) {
        DefaultFeatureCollection dfc = new DefaultFeatureCollection();
        dfc.addAll(features);
        return dfc.getBounds();
    }
}
