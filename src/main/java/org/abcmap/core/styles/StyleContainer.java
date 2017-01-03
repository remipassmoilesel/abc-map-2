package org.abcmap.core.styles;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;
import org.abcmap.core.draw.builder.AbmSimpleFeatureBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.styling.*;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import java.awt.*;
import java.util.Objects;

/**
 * Persistent style container
 * <p>
 */
@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "STYLES")
public class StyleContainer implements DataModel {

    private final static FilterFactory ff = FeatureUtils.getFilterFactory();

    private static final String ID_FIELD_NAME = "ID";
    private static final String FOREGROUND_FIELD_NAME = "FOREGROUND";
    private static final String BACKGROUND_FIELD_NAME = "BACKGROUND";
    private static final String THICK_FIELD_NAME = "THICK";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private Long id;

    @DatabaseField(columnName = FOREGROUND_FIELD_NAME)
    private String foreground;

    @DatabaseField(columnName = BACKGROUND_FIELD_NAME)
    private String background;

    @DatabaseField(columnName = THICK_FIELD_NAME)
    private int thick;

    private Rule rule;

    public StyleContainer() {

    }

    public StyleContainer(Color foreground, Color background, int thick) {
        this.foreground = Utils.colorToString(foreground);
        this.background = Utils.colorToString(background);
        this.thick = thick;
        generateId();
    }

    public StyleContainer(StyleContainer other) {
        this.id = other.id;
        this.foreground = other.foreground;
        this.background = other.background;
        this.thick = other.thick;
    }

    /**
     * Return a rule, tht can be added to a layer style
     *
     * @return
     */
    public Rule getRule() {

        if (this.rule == null) {
            rule = generateRule(this);
        }

        return rule;
    }

    /**
     * Foreground color used to draw
     *
     * @return
     */
    public Color getForeground() {
        return Utils.stringToColor(foreground);
    }

    /**
     * Background color used to draw
     *
     * @return
     */
    public Color getBackground() {
        return Utils.stringToColor(background);
    }

    /**
     * Thickness of the stroke used to draw
     *
     * @return
     */
    public int getThick() {
        return thick;
    }

    /**
     * Get the unique id of this style
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    public Long generateId() {
        this.id = System.nanoTime();
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleContainer that = (StyleContainer) o;
        return thick == that.thick &&
                Objects.equals(foreground, that.foreground) &&
                Objects.equals(background, that.background);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foreground, background, thick);
    }

    @Override
    public String toString() {
        return "StyleContainer{" +
                "id='" + id + '\'' +
                ", foreground='" + foreground + '\'' +
                ", background='" + background + '\'' +
                ", thick=" + thick +
                '}';
    }

    /**
     * Generate a Geotools rule from a style container
     * <p>
     * This rule is a basic rule: it has all symbolizers (point, line, polygon), and is visible at all scales.
     *
     * @param container
     * @return
     */
    public static Rule generateRule(StyleContainer container) {

        Rule rule = FeatureUtils.createRuleFor(container.getForeground(), container.getBackground(), container.getThick());

        // apply on specified id
        Filter filter = ff.equal(ff.property(AbmSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME), ff.literal(container.getId()), true);
        rule.setFilter(filter);

        return rule;

    }

}
