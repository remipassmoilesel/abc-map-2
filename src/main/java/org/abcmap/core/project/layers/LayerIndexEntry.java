package org.abcmap.core.project.layers;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;

import java.util.Objects;

/**
 * Object representing layer metadata. Metadata is stored separately in database.
 */

@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "LAYER_INDEX")
public class LayerIndexEntry implements DataModel {

    private static final String ID_FIELD_NAME = "ID";
    private static final String TYPE_FIELD_NAME = "TYPE";
    private static final String NAME_FIELD_NAME = "NAME";
    private static final String VISIBLE_FIELD_NAME = "VISIBLE";
    private static final String ZINDEX_FIELD_NAME = "ZINDEX";
    private static final String OPACITY_FIELD_NAME = "OPACITY";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private String layerId;

    @DatabaseField(columnName = TYPE_FIELD_NAME)
    private LayerType type;

    @DatabaseField(columnName = NAME_FIELD_NAME)
    private String name;

    @DatabaseField(columnName = VISIBLE_FIELD_NAME)
    private boolean visible;

    @DatabaseField(columnName = ZINDEX_FIELD_NAME)
    private int zindex;

    @DatabaseField(columnName = OPACITY_FIELD_NAME)
    private double opacity;

    public LayerIndexEntry() {
    }

    public LayerIndexEntry(String layerId, String name, boolean visible, int zindex, LayerType type) {

        this.name = name;
        this.visible = visible;
        this.zindex = zindex;
        this.type = type;
        this.opacity = 1;

        if (layerId == null) {
            generateNewId();
        } else {
            this.layerId = layerId;
        }

    }

    /**
     * Return type of lyaer
     *
     * @return
     */
    public LayerType getType() {
        return type;
    }

    /**
     * Set type of layer
     *
     * @param type
     */
    public void setType(LayerType type) {
        this.type = type;
    }

    /**
     * Return layer unique id
     *
     * @return
     */
    public String getLayerId() {
        return layerId;
    }

    /**
     * Set layer unique id
     *
     * @param layerId
     */
    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    /**
     * Get readable name of layer
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set readable name of layer
     *
     * @return
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return true if layer is visible
     *
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set layer visible or not
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get zindex of layer, 0 on bottom
     *
     * @return
     */
    public int getZindex() {
        return zindex;
    }

    /**
     * Set zindex of layer, 0 is bottom
     *
     * @param zindex
     */
    public void setZindex(int zindex) {
        this.zindex = zindex;
    }

    /**
     * Set opacity of layer between 0 and 1
     *
     * @param opacity
     */
    public void setOpacity(double opacity) {

        if (opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Invalid opacity: " + opacity);
        }

        this.opacity = opacity;
    }

    /**
     * Get opacity of layer between 0 and 1
     *
     * @return
     */
    public double getOpacity() {
        return opacity;
    }

    /**
     * Generate a new id for this layer entry, associated with its type
     */
    public void generateNewId() {

        String prefix = null;
        if (getType() != null) {
            prefix = generateId(getType().toString().toLowerCase());
        }

        this.setLayerId(prefix);
    }

    /**
     * Generate a unique layer id with an optionnal prefix
     *
     * @param prefix
     * @return
     */
    public static final String generateId(String prefix) {

        if (prefix == null) {
            prefix = "";
        } else {
            prefix += "_";
        }
        return "ABM_LAYER_" + prefix.toUpperCase() + System.nanoTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayerIndexEntry that = (LayerIndexEntry) o;
        return visible == that.visible &&
                zindex == that.zindex &&
                Double.compare(that.opacity, opacity) == 0 &&
                Objects.equals(layerId, that.layerId) &&
                type == that.type &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layerId, type, name, visible, zindex, opacity);
    }
}
