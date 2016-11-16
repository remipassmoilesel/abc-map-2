package org.abcmap.core.project.layer;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.project.dao.DataModel;

/**
 * Object that represent layer metadata. They are stored separately in database.
 */

@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "layer_index")
public class LayerIndexEntry implements DataModel{

    private static final String ID_FIELD_NAME = "id";
    private static final String TYPE_FIELD_NAME = "type";
    private static final String NAME_FIELD_NAME = "name";
    private static final String VISIBLE_FIELD_NAME = "visible";
    private static final String ZINDEX_FIELD_NAME = "zindex";

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

    public LayerIndexEntry() {
    }

    public LayerIndexEntry(String layerId, String name, boolean visible, int zindex, LayerType type) {
        if (layerId == null) {
            generateNewIndex();
        } else {
            this.layerId = layerId;
        }
        this.name = name;
        this.visible = visible;
        this.zindex = zindex;
        this.type = type;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }

    public String getLayerId() {
        return layerId;
    }

    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getZindex() {
        return zindex;
    }

    public void setZindex(int zindex) {
        this.zindex = zindex;
    }

    public void generateNewIndex() {
        this.setLayerId(generateId());
    }

    public static final String generateId() {
        return "abcmap_layerid_" + System.nanoTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayerIndexEntry that = (LayerIndexEntry) o;

        if (visible != that.visible) return false;
        if (zindex != that.zindex) return false;
        if (type != that.type) return false;
        if (layerId != null ? !layerId.equals(that.layerId) : that.layerId != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (layerId != null ? layerId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (visible ? 1 : 0);
        result = 31 * result + zindex;
        return result;
    }
}
