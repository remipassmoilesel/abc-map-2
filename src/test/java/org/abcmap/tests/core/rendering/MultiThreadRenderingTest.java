package org.abcmap.tests.core.rendering;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.abcmap.core.draw.builder.AbmSimpleFeatureBuilder;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.data.FeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.Random;

/**
 * Created by remipassmoilesel on 23/12/16.
 */
public class MultiThreadRenderingTest {

    private static final GeometryFactory geom = GeoUtils.getGeometryFactory();
    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory ff = FeatureUtils.getFilterFactory();
    private static final Long FAKE_STYLE_ID = 1l;
    private static CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;

    public static void main(String[] args) throws IOException {

        // create random features
        int featureNumber = 50;
        ReferencedEnvelope bounds = new ReferencedEnvelope(-500, 2000, -500, 2000, crs);

//        MapContent mapContent = prepareMapWithoutDatabase(featureNumber, bounds);
//        testWithSeparatedThreads(mapContent);
//
//        MapContent mapContent = prepareMapWithoutDatabase(featureNumber, bounds);
//        testWithSynchronizedRenderer(mapContent);
//
//        MapContent mapContent = prepareMapWithoutDatabase(featureNumber, bounds);
//        testWithThreadPool(mapContent);

        MapContent mapContent = prepareMapWithDatabase(featureNumber, bounds);
        testWithThreadPool(mapContent);


    }

    private static MapContent prepareMapWithDatabase(int featureNumber, ReferencedEnvelope bounds) throws IOException {

        PrimitiveIterator.OfDouble rand = new Random().doubles(bounds.getMinX(), bounds.getMaxX()).iterator();
        AbmSimpleFeatureBuilder builder = new AbmSimpleFeatureBuilder("default", crs);

        Path databasePath = Paths.get("tmp/rendererTest_" + System.currentTimeMillis());
        JDBCDataStore datastore = SQLUtils.getGeotoolsDatastoreFromH2(databasePath);
        datastore.createSchema(builder.getCurrentFeatureType());
        FeatureStore featureStore = (FeatureStore) datastore.getFeatureSource(builder.getCurrentFeatureType().getTypeName());

        DefaultFeatureCollection features = new DefaultFeatureCollection();

        for (int i = 0; i < featureNumber; i++) {
            ArrayList<Coordinate> points = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                points.add(new Coordinate(rand.next(), rand.next()));
            }

            LineString shape = geom.createLineString(points.toArray(new Coordinate[]{}));
            features.add(builder.build(shape, FAKE_STYLE_ID));
        }
        featureStore.addFeatures(features);

        // create a fake map
        FeatureLayer layer = new FeatureLayer(featureStore, generateStyle());
        MapContent mapContent = new MapContent();
        mapContent.addLayer(layer);

        return mapContent;
    }

    private static MapContent prepareMapWithoutDatabase(int featureNumber, ReferencedEnvelope bounds) {

        PrimitiveIterator.OfDouble rand = new Random().doubles(bounds.getMinX(), bounds.getMaxX()).iterator();
        DefaultFeatureCollection coll = new DefaultFeatureCollection();
        AbmSimpleFeatureBuilder builder = new AbmSimpleFeatureBuilder("default", crs);

        for (int i = 0; i < featureNumber; i++) {
            ArrayList<Coordinate> points = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                points.add(new Coordinate(rand.next(), rand.next()));
            }

            LineString shape = geom.createLineString(points.toArray(new Coordinate[]{}));
            coll.add(builder.build(shape, FAKE_STYLE_ID));
        }

        // create a fake map
        FeatureLayer layer = new FeatureLayer(coll, generateStyle());
        MapContent mapContent = new MapContent();
        mapContent.addLayer(layer);

        return mapContent;
    }

    private static void testWithSynchronizedRenderer(MapContent mapContent) {

        // 0: mini, 1: maxi
        long[] minMax = new long[]{-1, -1};

        StreamingRenderer renderer = GeoUtils.buildRenderer();

        // launch twelve tasks
        for (int i = 0; i < 12; i++) {
            // TODO try with thread manager
            ThreadManager.runLater(() -> {

                // each thread render 5 tiles

                int width = 500;
                int height = 500;

                synchronized (renderer) {
                    renderer.setMapContent(mapContent);

                    for (int j = 0; j < 100; j++) {
                        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                        long before = System.currentTimeMillis();
                        renderer.paint((Graphics2D) img.getGraphics(), new Rectangle(width, height), mapContent.getMaxBounds());

                        long renderTime = System.currentTimeMillis() - before;
                        if (minMax[0] < 0 || minMax[0] > renderTime) {
                            minMax[0] = renderTime;
                        }

                        if (minMax[1] < 0 || minMax[1] < renderTime) {
                            minMax[1] = renderTime;
                        }


                        //System.out.println("Rendering time: " + renderTime + "ms");
                        //GuiUtils.showImage(img);
                    }
                }

                System.out.println("### Min: " + minMax[0]);
                System.out.println("### Max: " + minMax[1]);

            });
        }
    }

// Test with synchronized renderer, without database, very slow obviously (break multithreading advantages)
//        ### Min: 64
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357
//        ### Min: 63
//        ### Max: 357

    private static void testWithThreadPool(MapContent mapContent) {

        // 0: mini, 1: maxi
        long[] minMax = new long[]{-1, -1};

        // launch three thread
        for (int i = 0; i < 12; i++) {
            // TODO try with thread manager
            ThreadManager.runLater(() -> {

                // each thread render 5 tiles

                int width = 500;
                int height = 500;

                StreamingRenderer renderer = GeoUtils.buildRenderer();
                renderer.setMapContent(mapContent);

                for (int j = 0; j < 100; j++) {
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                    long before = System.currentTimeMillis();
                    renderer.paint((Graphics2D) img.getGraphics(), new Rectangle(width, height), mapContent.getMaxBounds());

                    long renderTime = System.currentTimeMillis() - before;
                    if (minMax[0] < 0 || minMax[0] > renderTime) {
                        minMax[0] = renderTime;
                    }

                    if (minMax[1] < 0 || minMax[1] < renderTime) {
                        minMax[1] = renderTime;
                    }


                    //System.out.println("Rendering time: " + renderTime + "ms");
                    //GuiUtils.showImage(img);
                }


                System.out.println("### Min: " + minMax[0]);
                System.out.println("### Max: " + minMax[1]);

            });
        }
    }

//        Using thread pool, without database
//
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 133
//        ### Max: 1048
//        ### Min: 115
//        ### Max: 1048
//        ### Min: 101
//        ### Max: 1048
//        ### Min: 77
//        ### Max: 1048
//        ### Min: 76
//        ### Max: 1048

//    ### Min: 143
//            ### Max: 1268
//            ### Min: 143
//            ### Max: 1268
//            ### Min: 143
//            ### Max: 1268
//            ### Min: 143
//            ### Max: 1268
//            ### Min: 143
//            ### Max: 1268
//            ### Min: 143
//            ### Max: 1268
//            ### Min: 143
//            ### Max: 1268
//            ### Min: 137
//            ### Max: 1268
//            ### Min: 122
//            ### Max: 1268
//            ### Min: 86
//            ### Max: 1268
//            ### Min: 80
//            ### Max: 1268
//            ### Min: 80
//            ### Max: 1268

    // Test with thread pool, with database
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 141
//        ### Max: 1103
//        ### Min: 117
//        ### Max: 1103
//        ### Min: 117
//        ### Max: 1103
//        ### Min: 76
//        ### Max: 1103
//        ### Min: 72
//        ### Max: 1103

    private static void testWithSeparatedThreads(MapContent mapContent) {

        // 0: mini, 1: maxi
        long[] minMax = new long[]{-1, -1};

        // launch twelve thread
        for (int i = 0; i < 12; i++) {
            // TODO try with thread manager
            new Thread(() -> {

                // each thread render 5 tiles

                int width = 500;
                int height = 500;

                StreamingRenderer renderer = GeoUtils.buildRenderer();
                renderer.setMapContent(mapContent);

                for (int j = 0; j < 100; j++) {
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                    long before = System.currentTimeMillis();
                    renderer.paint((Graphics2D) img.getGraphics(), new Rectangle(width, height), mapContent.getMaxBounds());

                    long renderTime = System.currentTimeMillis() - before;
                    if (minMax[0] < 0 || minMax[0] > renderTime) {
                        minMax[0] = renderTime;
                    }

                    if (minMax[1] < 0 || minMax[1] < renderTime) {
                        minMax[1] = renderTime;
                    }


                    //System.out.println("Rendering time: " + renderTime + "ms");
                    //GuiUtils.showImage(img);
                }


                System.out.println("### Min: " + minMax[0]);
                System.out.println("### Max: " + minMax[1]);

            }).start();
        }
    }

//        Using separate threads
//        /usr/lib/jvm/java-8-openjdk-amd64/bin/java -Didea.launcher.port=7540 -Didea.launcher.bin.path=/home/remipassmoilesel/intellij/bin -Dfile.encoding=UTF-8 -classpath /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/charsets.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/dnsns.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/icedtea-sound.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/jaccess.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/localedata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/nashorn.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunec.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/zipfs.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jce.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jsse.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/management-agent.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/resources.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/remipassmoilesel/projects/java/abcmap2-reboot/target/test-classes:/home/remipassmoilesel/projects/java/abcmap2-reboot/target/classes:/home/remipassmoilesel/.m2/repository/org/reflections/reflections/0.9.10/reflections-0.9.10.jar:/home/remipassmoilesel/.m2/repository/com/google/guava/guava/15.0/guava-15.0.jar:/home/remipassmoilesel/.m2/repository/org/javassist/javassist/3.19.0-GA/javassist-3.19.0-GA.jar:/home/remipassmoilesel/.m2/repository/com/google/code/findbugs/annotations/2.0.1/annotations-2.0.1.jar:/home/remipassmoilesel/.m2/repository/com/thoughtworks/xstream/xstream/1.4.9/xstream-1.4.9.jar:/home/remipassmoilesel/.m2/repository/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar:/home/remipassmoilesel/.m2/repository/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar:/home/remipassmoilesel/.m2/repository/junit/junit/4.12/junit-4.12.jar:/home/remipassmoilesel/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-swing/16.0/gt-swing-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-referencing/16.0/gt-referencing-16.0.jar:/home/remipassmoilesel/.m2/repository/com/googlecode/efficient-java-matrix-library/core/0.26/core-0.26.jar:/home/remipassmoilesel/.m2/repository/commons-pool/commons-pool/1.5.4/commons-pool-1.5.4.jar:/home/remipassmoilesel/.m2/repository/jgridshift/jgridshift/1.0/jgridshift-1.0.jar:/home/remipassmoilesel/.m2/repository/net/sf/geographiclib/GeographicLib-Java/1.44/GeographicLib-Java-1.44.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-render/16.0/gt-render-16.0.jar:/home/remipassmoilesel/.m2/repository/com/miglayout/miglayout/3.7/miglayout-3.7-swing.jar:/home/remipassmoilesel/.m2/repository/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-shapefile/16.0/gt-shapefile-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-data/16.0/gt-data-16.0.jar:/home/remipassmoilesel/.m2/repository/org/jdom/jdom/1.1.3/jdom-1.1.3.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-epsg-hsql/16.0/gt-epsg-hsql-16.0.jar:/home/remipassmoilesel/.m2/repository/org/hsqldb/hsqldb/2.3.0/hsqldb-2.3.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-svg/16.0/gt-svg-16.0.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-transcoder/1.7/batik-transcoder-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/fop/0.94/fop-0.94.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/xmlgraphics-commons/1.2/xmlgraphics-commons-1.2.jar:/home/remipassmoilesel/.m2/repository/org/apache/avalon/framework/avalon-framework-api/4.3.1/avalon-framework-api-4.3.1.jar:/home/remipassmoilesel/.m2/repository/org/apache/avalon/framework/avalon-framework-impl/4.3.1/avalon-framework-impl-4.3.1.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-awt-util/1.7/batik-awt-util-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-bridge/1.7/batik-bridge-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-anim/1.7/batik-anim-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-css/1.7/batik-css-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-ext/1.7/batik-ext-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-parser/1.7/batik-parser-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-script/1.7/batik-script-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-js/1.7/batik-js-1.7.jar:/home/remipassmoilesel/.m2/repository/xalan/xalan/2.6.0/xalan-2.6.0.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-dom/1.7/batik-dom-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-gvt/1.7/batik-gvt-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-svg-dom/1.7/batik-svg-dom-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-svggen/1.7/batik-svggen-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-util/1.7/batik-util-1.7.jar:/home/remipassmoilesel/.m2/repository/org/apache/xmlgraphics/batik-xml/1.7/batik-xml-1.7.jar:/home/remipassmoilesel/.m2/repository/xml-apis/xml-apis/1.3.04/xml-apis-1.3.04.jar:/home/remipassmoilesel/.m2/repository/xml-apis/xml-apis-ext/1.3.04/xml-apis-ext-1.3.04.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-geotiff/16.0/gt-geotiff-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-main/16.0/gt-main-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-coverage/16.0/gt-coverage-16.0.jar:/home/remipassmoilesel/.m2/repository/org/jaitools/jt-zonalstats/1.4.0/jt-zonalstats-1.4.0.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/affine/jt-affine/1.0.11/jt-affine-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/algebra/jt-algebra/1.0.11/jt-algebra-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/bandmerge/jt-bandmerge/1.0.11/jt-bandmerge-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/bandselect/jt-bandselect/1.0.11/jt-bandselect-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/bandcombine/jt-bandcombine/1.0.11/jt-bandcombine-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/border/jt-border/1.0.11/jt-border-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/buffer/jt-buffer/1.0.11/jt-buffer-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/crop/jt-crop/1.0.11/jt-crop-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/iterators/jt-iterators/1.0.11/jt-iterators-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/lookup/jt-lookup/1.0.11/jt-lookup-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/mosaic/jt-mosaic/1.0.11/jt-mosaic-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/nullop/jt-nullop/1.0.11/jt-nullop-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/rescale/jt-rescale/1.0.11/jt-rescale-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/scale/jt-scale/1.0.11/jt-scale-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/stats/jt-stats/1.0.11/jt-stats-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/translate/jt-translate/1.0.11/jt-translate-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/utilities/jt-utilities/1.0.11/jt-utilities-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/warp/jt-warp/1.0.11/jt-warp-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/zonal/jt-zonal/1.0.11/jt-zonal-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/binarize/jt-binarize/1.0.11/jt-binarize-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/format/jt-format/1.0.11/jt-format-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/colorconvert/jt-colorconvert/1.0.11/jt-colorconvert-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/errordiffusion/jt-errordiffusion/1.0.11/jt-errordiffusion-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/orderdither/jt-orderdither/1.0.11/jt-orderdither-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/colorindexer/jt-colorindexer/1.0.11/jt-colorindexer-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/imagefunction/jt-imagefunction/1.0.11/jt-imagefunction-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/piecewise/jt-piecewise/1.0.11/jt-piecewise-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/classifier/jt-classifier/1.0.11/jt-classifier-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/rlookup/jt-rlookup/1.0.11/jt-rlookup-1.0.11.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/jaiext/vectorbin/jt-vectorbin/1.0.11/jt-vectorbin-1.0.11.jar:/home/remipassmoilesel/.m2/repository/javax/media/jai_imageio/1.1/jai_imageio-1.1.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/imageio-ext/imageio-ext-tiff/1.1.16/imageio-ext-tiff-1.1.16.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/imageio-ext/imageio-ext-utilities/1.1.16/imageio-ext-utilities-1.1.16.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-image/16.0/gt-image-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-wms/16.0/gt-wms-16.0.jar:/home/remipassmoilesel/.m2/repository/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar:/home/remipassmoilesel/.m2/repository/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar:/home/remipassmoilesel/.m2/repository/commons-codec/commons-codec/1.2/commons-codec-1.2.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-xml/16.0/gt-xml-16.0.jar:/home/remipassmoilesel/.m2/repository/org/apache/xml/xml-commons-resolver/1.2/xml-commons-resolver-1.2.jar:/home/remipassmoilesel/.m2/repository/commons-io/commons-io/2.1/commons-io-2.1.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-imagemosaic/16.0/gt-imagemosaic-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-api/16.0/gt-api-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-jdbc/16.0/gt-jdbc-16.0.jar:/home/remipassmoilesel/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar:/home/remipassmoilesel/.m2/repository/com/vividsolutions/jts/1.13/jts-1.13.jar:/home/remipassmoilesel/.m2/repository/net/java/dev/jsr-275/jsr-275/1.0-beta-2/jsr-275-1.0-beta-2.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-transform/16.0/gt-transform-16.0.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/imageio-ext/imageio-ext-streams/1.1.16/imageio-ext-streams-1.1.16.jar:/home/remipassmoilesel/.m2/repository/it/geosolutions/imageio-ext/imageio-ext-geocore/1.1.16/imageio-ext-geocore-1.1.16.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-cql/16.0/gt-cql-16.0.jar:/home/remipassmoilesel/.m2/repository/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2-noclassprop.jar:/home/remipassmoilesel/.m2/repository/org/jaitools/jt-utils/1.4.0/jt-utils-1.4.0.jar:/home/remipassmoilesel/.m2/repository/org/jaitools/jt-vectorbinarize/1.4.0/jt-vectorbinarize-1.4.0.jar:/home/remipassmoilesel/.m2/repository/net/sf/ehcache/ehcache/1.6.2/ehcache-1.6.2.jar:/home/remipassmoilesel/.m2/repository/javax/media/jai_codec/1.1.3/jai_codec-1.1.3.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-metadata/16.0/gt-metadata-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-opengis/16.0/gt-opengis-16.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/jdbc/gt-jdbc-postgis/16.0/gt-jdbc-postgis-16.0.jar:/home/remipassmoilesel/.m2/repository/org/postgresql/postgresql/9.4.1211/postgresql-9.4.1211.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-imagepyramid/16.0/gt-imagepyramid-16.0.jar:/home/remipassmoilesel/.m2/repository/com/j256/ormlite/ormlite-jdbc/5.0/ormlite-jdbc-5.0.jar:/home/remipassmoilesel/.m2/repository/com/j256/ormlite/ormlite-core/5.0/ormlite-core-5.0.jar:/home/remipassmoilesel/.m2/repository/org/geotools/gt-imagemosaic-jdbc/17-SNAPSHOT/gt-imagemosaic-jdbc-17-SNAPSHOT.jar:/home/remipassmoilesel/.m2/repository/org/apache/ant/ant/1.8.1/ant-1.8.1.jar:/home/remipassmoilesel/.m2/repository/org/apache/ant/ant-launcher/1.8.1/ant-launcher-1.8.1.jar:/home/remipassmoilesel/.m2/repository/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.jar:/home/remipassmoilesel/.m2/repository/org/geotools/jdbc/gt-jdbc-h2/16.0/gt-jdbc-h2-16.0.jar:/home/remipassmoilesel/.m2/repository/org/opengeo/geodb/0.7-RC2/geodb-0.7-RC2.jar:/home/remipassmoilesel/.m2/repository/net/sourceforge/hatbox/hatbox/1.0.b7/hatbox-1.0.b7.jar:/home/remipassmoilesel/.m2/repository/com/h2database/h2/1.1.119/h2-1.1.119.jar:/home/remipassmoilesel/.m2/repository/org/apache/commons/commons-compress/1.12/commons-compress-1.12.jar:/home/remipassmoilesel/.m2/repository/com/labun/surf/1.0/surf-1.0.jar:/home/remipassmoilesel/.m2/repository/gov/nih/imagej/imagej/1.47/imagej-1.47.jar:/home/remipassmoilesel/intellij/lib/idea_rt.jar com.intellij.rt.execution.application.AppMain org.abcmap.tests.core.rendering.MultiThreadRenderingTest
//        ### Min: 173
//        ### Max: 1460
//        ### Min: 173
//        ### Max: 1460
//        ### Min: 173
//        ### Max: 1460
//        ### Min: 173
//        ### Max: 1460
//        ### Min: 173
//        ### Max: 1460
//        ### Min: 129
//        ### Max: 1460
//        ### Min: 129
//        ### Max: 1460
//        ### Min: 129
//        ### Max: 1460
//        ### Min: 114
//        ### Max: 1460
//        ### Min: 80
//        ### Max: 1460
//        ### Min: 80
//        ### Max: 1460
//        ### Min: 80
//        ### Max: 1460

    private static Style generateStyle() {

        // create point symbolizer
        Stroke stroke = sf.stroke(ff.literal(Color.black), null, null, null, null, null, null);
        Fill fill = sf.fill(null, ff.literal(Color.red), ff.literal(1.0));

        Mark mark = sf.getCircleMark();
        mark.setFill(fill);
        mark.setStroke(stroke);

        Graphic graphic = sf.createDefaultGraphic();
        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(mark);
        graphic.setSize(ff.literal(5));

        // here we can specify name of geometry field. Set to null allow to not specify it
        PointSymbolizer pointSym = sf.createPointSymbolizer(graphic, null);

        // create line symbolizer
        LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);

        // create polygon symbolizer
        PolygonSymbolizer polygonSym = sf.createPolygonSymbolizer(stroke, fill, null);

        // create rule
        Rule r = sf.createRule();
        r.symbolizers().add(pointSym);
        r.symbolizers().add(lineSym);
        r.symbolizers().add(polygonSym);

        // apply on specified id
        Filter filter = ff.equal(ff.property(AbmSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME), ff.literal(FAKE_STYLE_ID), true);
        r.setFilter(filter);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(sf.createFeatureTypeStyle(new Rule[]{r}));

        return style;

    }

}
