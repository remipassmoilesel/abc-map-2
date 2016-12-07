package org.abcmap.core.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ContrastMethod;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by remipassmoilesel on 10/11/16.
 */
public class GeoUtils {

    private static final FilterFactory ff = FeatureUtils.getFilterFactory();
    private static final StyleFactory sf = FeatureUtils.getStyleFactory();
    public static final CustomLogger logger = LogManager.getLogger(GeoUtils.class);

    public static final CoordinateReferenceSystem WGS_84 = DefaultGeographicCRS.WGS84;

    /**
     * Generic 2D coordinate reference system. Prefer use of this instead of
     * DefaultEngineeringSystem.GENERIC2D because it can be a lot slower.
     */
    public static CoordinateReferenceSystem GENERIC_2D;

    static {
        try {
            GENERIC_2D = CRS.decode("EPSG:404000");
        } catch (FactoryException e) {
            logger.error(e);
        }
    }

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

        StreamingRenderer renderer = new StreamingRenderer();

        RenderingHints javaHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderer.setJava2DHints(javaHints);

        renderer.addRenderListener(new RenderListener() {

            @Override
            public void featureRenderer(SimpleFeature feature) {
//                System.out.println(feature);
            }

            @Override
            public void errorOccurred(Exception e) {
                System.out.println(e);
            }
        });

        return renderer;
    }

    public static Point2D coordinateToPoint2D(Coordinate coordinate) {
        return new Point2D.Double(coordinate.x, coordinate.y);
    }

    public static boolean isMapContains(MapContent map, Layer layer) {
        for (Layer lay : map.layers()) {
            if (lay.equals(layer)) {
                return true;
            }
        }

        return false;
    }
}
