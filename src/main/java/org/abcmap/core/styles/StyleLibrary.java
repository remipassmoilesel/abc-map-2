package org.abcmap.core.styles;

import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

import java.awt.*;
import java.util.*;

/**
 * Store and create styles
 */
public class StyleLibrary {

    protected final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final ProjectManager projectMan;
    private final HashMap<String, FeatureTypeStyle> ftstyles;
    private ArrayList<StyleContainer> styleCollection;

    public StyleLibrary() {
        projectMan = Main.getProjectManager();
        styleCollection = new ArrayList<>();

        ftstyles = new HashMap<String, FeatureTypeStyle>();
    }

    /**
     * Build a feature type style and store it
     *
     * @param type
     * @param rules
     */
    public FeatureTypeStyle getFeatureTypeStyle(FeatureType type, Rule... rules) {

        // search for existing feature type style
        String fsid = type.getName().toString();
        FeatureTypeStyle fts = ftstyles.get(fsid);

        // feature type style does not exist, create it
        if (fts == null) {
            FeatureTypeStyle featureTypeStyle = sf.createFeatureTypeStyle(rules);
            ftstyles.put(fsid, featureTypeStyle);
            fts = featureTypeStyle;
        }

        // feature type style exist, add rules
        else {
            fts.rules().addAll(Arrays.asList(rules));
        }

        return fts;

    }

    public Collection<FeatureTypeStyle> getFeatureStyles() {
        return ftstyles.values();
    }

    /**
     * Return a style container corresponding to these characteristics
     *
     * @param foreground
     * @param background
     * @param thick
     * @return
     */
    public StyleContainer getStyle(Color foreground, Color background, int thick) {

        StyleContainer container = new StyleContainer(foreground, background, thick);
        int index = styleCollection.indexOf(container);
        if (index != -1) {
            return styleCollection.get(index);
        } else {
            styleCollection.add(container);
            return container;
        }

    }

    /**
     * Return the entire list of styles
     *
     * @return
     */
    public ArrayList<StyleContainer> getStyleCollection() {
        return styleCollection;
    }

    /**
     * Replace the present list of styles
     *
     * @param styleCollection
     */
    public void setStyleCollection(ArrayList<StyleContainer> styleCollection) {
        this.styleCollection = styleCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleLibrary that = (StyleLibrary) o;
        return Objects.equals(styleCollection, that.styleCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(styleCollection);
    }

    /**
     * Apply a style to a feature and a layer
     *  @param style
     * @param layer
     * @param features
     */
    public FeatureTypeStyle applyStyle(StyleContainer style, AbstractLayer layer, SimpleFeature... features) {
        return applyStyle(style, (FeatureLayer) layer, features);
    }

    /**
     * Apply a style to a feature and a layer
     *  @param style
     * @param layer
     * @param features
     */
    public FeatureTypeStyle applyStyle(StyleContainer style, FeatureLayer layer, SimpleFeature... features) {

        if (style == null) {
            throw new NullPointerException("Style cannot be null");
        }

        if (features == null) {
            throw new NullPointerException("Feature cannot be null");
        }

        // apply style to features
        FeatureUtils.applyStyleToFeatures(style, features);

        // get feature type style associated with feature type
        FeatureTypeStyle fts = getFeatureTypeStyle(features[0].getType(), style.getRule());

        // add fts
        layer.getLayerStyle().featureTypeStyles().add(fts);

        return fts;
    }
}
