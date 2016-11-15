package org.abcmap.core.styles;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.project.dao.DataModel;
import org.abcmap.core.utils.Utils;

import java.awt.*;
import java.util.Objects;

/**
 * Persistent style
 */
@DatabaseTable(tableName = "styles")
public class StyleContainer implements DataModel {

    private static final String ID_FIELD_NAME = "id";
    private static final String TYPE_FIELD_NAME = "type";
    private static final String FOREGROUND_FIELD_NAME = "foreground";
    private static final String BACKGROUND_FIELD_NAME = "background";
    private static final String THICK_FIELD_NAME = "thick";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private String id;

    @DatabaseField(columnName = TYPE_FIELD_NAME)
    private StyleType type;

    @DatabaseField(columnName = FOREGROUND_FIELD_NAME)
    private String foreground;

    @DatabaseField(columnName = BACKGROUND_FIELD_NAME)
    private String background;

    @DatabaseField(columnName = THICK_FIELD_NAME)
    private int thick;

    public StyleContainer() {

    }

    public StyleContainer(StyleType type, Color foreground, Color background, int thick) {
        this.type = type;
        this.foreground = Utils.colorToString(foreground);
        this.background = Utils.colorToString(background);
        this.thick = thick;
        generateId();
    }

    public StyleContainer(StyleContainer other) {
        this.id = other.id;
        this.type = other.type;
        this.foreground = other.foreground;
        this.background = other.background;
        this.thick = other.thick;
    }

    /**
     * Style type: line, point, ....
     *
     * @return
     */
    public StyleType getType() {
        return type;
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
    public String getId() {
        return id;
    }

    public String generateId() {
        this.id = getType().toString() + "_" + System.nanoTime();
        return this.id;
    }

    /**
     * Data used: type, foreground, background, thick
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleContainer that = (StyleContainer) o;
        return thick == that.thick &&
                type == that.type &&
                Objects.equals(foreground, that.foreground) &&
                Objects.equals(background, that.background);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, foreground, background, thick);
    }

    @Override
    public String toString() {
        return "StyleContainer{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", foreground='" + foreground + '\'' +
                ", background='" + background + '\'' +
                ", thick=" + thick +
                '}';
    }
}
