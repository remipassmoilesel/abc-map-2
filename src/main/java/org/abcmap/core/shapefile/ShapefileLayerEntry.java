package org.abcmap.core.shapefile;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;

import java.util.Objects;

/**
 * Allow to store WMS information on layers
 */
@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "SHAPEFILE_INDEX")
public class ShapefileLayerEntry implements DataModel {

    private static final String ID_FIELD_NAME = "ID";
    private static final String PATH_FIELD_NAME = "PATH_FIELD_NAME";
    private static final String STYLE_ID = "STYLE_ID";

    @DatabaseField(id = true, columnName = ID_FIELD_NAME)
    private String layerId;

    @DatabaseField(columnName = PATH_FIELD_NAME)
    private String path;

    @DatabaseField(columnName = STYLE_ID)
    private Long styleId;

    public ShapefileLayerEntry() {

    }

    public ShapefileLayerEntry(String layerId, String path, Long styleId) {
        this.layerId = layerId;
        this.path = path;
        this.styleId = styleId;
    }

    /**
     * Return layer id of this shape file layer
     *
     * @return
     */
    public String getLayerId() {
        return layerId;
    }

    /**
     * Set the layer id of this shapefile layer
     *
     * @param layerId
     */
    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    /**
     * Get path of displayed shape file
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Set path of displayed shape file
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get style id associated with this shape file
     *
     * @return
     */
    public Long getStyleId() {
        return styleId;
    }

    /**
     * Get style id associated with this shape file
     *
     * @param styleId
     */
    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShapefileLayerEntry that = (ShapefileLayerEntry) o;
        return Objects.equals(layerId, that.layerId) &&
                Objects.equals(path, that.path) &&
                Objects.equals(styleId, that.styleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layerId, path, styleId);
    }

    @Override
    public String toString() {
        return "ShapefileLayerEntry{" +
                "layerId='" + layerId + '\'' +
                ", path='" + path + '\'' +
                ", styleId=" + styleId +
                '}';
    }
}
