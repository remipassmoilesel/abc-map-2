package org.abcmap.tests.demonstrations;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.Random;

/**
 * Streaming renderer do not close all map content it open, when work is interrupted
 * <p>
 * Here the only map content created is not finalized, but some error messages will appear (GRAVE: Call MapContent dispose() to prevent memory leaks)
 * <p>
 * To test it, use low heap memory configuration (-Xmx128m)
 */
public class StreamingRendererMapContentIssue extends MapContent {

    private static final StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private static final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    private static final GeometryFactory geom = JTSFactoryFinder.getGeometryFactory();

    public static void main(String[] args) throws Throwable {

        // create a random content
        MapContent content = new MapContent();

        DefaultFeatureCollection coll = new DefaultFeatureCollection();
        FeatureLayer layer = new FeatureLayer(coll, createDefaultStyle());
        FeatureLayer layer2 = new FeatureLayer(coll, createDefaultStyle());
        content.addLayer(layer);
        content.addLayer(layer2);

        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName("feature1");
        tbuilder.setCRS(DefaultGeographicCRS.WGS84);
        tbuilder.add("geometry", Geometry.class);
        tbuilder.add("name", String.class);

        SimpleFeatureType type = tbuilder.buildFeatureType();
        SimpleFeatureBuilder fbuilder = new SimpleFeatureBuilder(type);

        // populate with random features
        int featureNumber = 50;
        ReferencedEnvelope bounds = new ReferencedEnvelope(-20, 20, -30, 30, DefaultGeographicCRS.WGS84);
        PrimitiveIterator.OfDouble rand = new Random().doubles(bounds.getMinX(), bounds.getMaxX()).iterator();
        for (int i = 0; i < featureNumber; i++) {

            ArrayList<Coordinate> points = new ArrayList();
            for (int j = 0; j < 5; j++) {
                points.add(new Coordinate(rand.next(), rand.next()));
            }

            fbuilder.add(geom.createLineString(points.toArray(new Coordinate[points.size()])));
            fbuilder.add("Feature " + i);
            coll.add(fbuilder.buildFeature(null));
        }

        // uncomment to test heap space (OutOfMemoryError)
        //ArrayList<Object> images = new ArrayList<>();

        // render a lot of time
        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            StreamingRenderer renderer = new StreamingRenderer();
            renderer.setMapContent(content);

            // stop rendering after 10 features
            renderer.addRenderListener(new RenderListener() {
                int count = 0;

                @Override
                public void featureRenderer(SimpleFeature feature) {
                    count++;
                    if (count > 10) {
                        renderer.stopRendering();
                        //System.err.println("Stop rendering !");
                    }
                }

                @Override
                public void errorOccurred(Exception e) {
                }
            });

            Rectangle area = new Rectangle(0, 0, 2000, 2000);
            BufferedImage img = new BufferedImage(area.width, area.height, BufferedImage.TYPE_INT_ARGB);

            // long before = System.currentTimeMillis();
            try {
                renderer.paint((Graphics2D) img.getGraphics(), area, bounds);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            // System.out.println("Rendering time: " + (System.currentTimeMillis() - before) + " ms");

            // uncomment to test heap space (OutOfMemoryError)
            // images.add(img);

        }


    }

    private static Style createDefaultStyle() {

        Color foreground = Color.darkGray;
        int thick = 3;

        // create stroke
        org.geotools.styling.Stroke stroke = sf.stroke(ff.literal(foreground), null, ff.literal(thick), null, null, null, null);

        // create line symbolizer
        LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);

        // create rule
        Rule r = sf.createRule();
        r.symbolizers().add(lineSym);

        // add it to style
        Style style = sf.createStyle();
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(r);
        style.featureTypeStyles().add(fts);

        return style;
    }


}
