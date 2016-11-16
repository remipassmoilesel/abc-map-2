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
import java.util.Objects;

/**
 * Store and create styles
 */
public class StyleLibrary {

    private final ProjectManager projectMan;
    private ArrayList<StyleContainer> styleCollection;

    public StyleLibrary() {
        projectMan = MainManager.getProjectManager();
        styleCollection = new ArrayList<>();
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
}
