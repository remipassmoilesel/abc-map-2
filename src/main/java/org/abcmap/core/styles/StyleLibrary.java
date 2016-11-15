package org.abcmap.core.styles;

import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.layer.Layer;
import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.styling.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Store and create styles
 */
public class StyleLibrary {

    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory ff = FeatureUtils.getFilterFactory();

    private final ArrayList<StyleContainer> styleCollection;
    private final ProjectManager projectMan;

    public StyleLibrary() {
        projectMan = MainManager.getProjectManager();
        styleCollection = new ArrayList<>();
    }

    /**
     * Return a style container corresponding to these characteristics
     *
     * @param type
     * @param line
     * @param fill
     * @param thick
     * @return
     */
    public StyleContainer getStyle(StyleType type, Color line, Color fill, int thick) {

        StyleContainer container = new StyleContainer(type, line, fill, thick);
        int index = styleCollection.indexOf(container);
        if (index != -1) {
            return styleCollection.get(index);
        } else {
            styleCollection.add(container);
            return container;
        }

    }

    /**
     * Apply a style to specified features
     *
     * @param features
     * @param style
     */
    public void applyStyle(StyleContainer style, SimpleFeature... features) {

        ArrayList<Layer> layers = projectMan.getProject().getLayers();
        for (SimpleFeature simpleFeature : features) {
            simpleFeature.setAttribute(DefaultFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME, style.getId());
        }

    }

    /**
     * Apply a style to specified features
     *
     * @param featureIds
     * @param style
     */
    public void applyStyle(StyleContainer style, String... featureIds) {

        HashSet<Identifier> ids = new HashSet(featureIds.length);

        for (String id : featureIds) {
            ids.add(new FeatureIdImpl(id));
        }

        applyStyle(style, ids);
    }

    /**
     * Apply a style to specified features
     *
     * @param featureIds
     * @param style
     */
    public void applyStyle(StyleContainer style, HashSet<Identifier> featureIds) {

        Id filter = ff.id(featureIds);

        ArrayList<Layer> layers = projectMan.getProject().getLayers();
        for (Layer lay : layers) {

            lay.executeVisit((SimpleFeature feature) -> {
                applyStyle(style, feature);
                return true;
            }, filter);

        }

    }

    /**
     * Build a Geotools rule with style container properties
     *
     * @param container
     * @return
     */
    public Rule getRuleFromStyle(StyleContainer container){

        if (StyleType.POINT == container.getType()) {

            org.geotools.styling.Stroke stroke = sf.stroke(ff.literal(container.getForeground()), null, null, null, null, null, null);
            Fill fill = sf.fill(null, ff.literal(container.getBackground()), ff.literal(1.0));

            Mark mark = sf.getCircleMark();
            mark.setFill(fill);
            mark.setStroke(stroke);

            Graphic graphic = sf.createDefaultGraphic();
            graphic.graphicalSymbols().clear();
            graphic.graphicalSymbols().add(mark);
            graphic.setSize(ff.literal(container.getThick()));

            // here we have to specify the name of the geometry field. Set to null allow to not specify it
            PointSymbolizer symbolizer = sf.createPointSymbolizer(graphic, null);

            Rule r = sf.createRule();
            r.symbolizers().add(symbolizer);
            Filter filter = ff.equal(ff.property(DefaultFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME), ff.literal(container.getId()), true);
            r.setFilter(filter);


            return r;
        } else {
            throw new IllegalStateException("Unrecognized style type: " + container.getType());
        }

    }


}
