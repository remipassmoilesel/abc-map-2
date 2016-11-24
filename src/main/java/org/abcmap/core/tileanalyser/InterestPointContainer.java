package org.abcmap.core.tileanalyser;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.labun.surf.InterestPoint;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Object containing matchable points for a image
 */
@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "TILE_INTEREST_POINTS")
public class InterestPointContainer implements DataModel {

    private static final String TILE_ID_FIELD_NAME = "TILE_ID";
    private static final String SURF_CONFIG_ID_FIELD_NAME = "SURF_CONFIG_ID";
    private static final String POINTS_FIELD_NAME = "POINTS";

    @DatabaseField(id = true, columnName = TILE_ID_FIELD_NAME)
    private String tileId;

    @DatabaseField(columnName = SURF_CONFIG_ID_FIELD_NAME)
    private Integer surfConfigId;

    @DatabaseField(columnName = POINTS_FIELD_NAME, dataType = DataType.SERIALIZABLE)
    private ArrayList<InterestPoint> points;

    public InterestPointContainer() {
    }

    /**
     * Return the image unique id
     *
     * @return
     */
    public String getTileId() {
        return tileId;
    }

    /**
     * Set the unique image id
     *
     * @param tileId
     */
    public void setTileId(String tileId) {
        this.tileId = tileId;
    }

    /**
     * Return the SURF configuration number used to process image
     *
     * @return
     */
    public Integer getSurfConfigId() {
        return surfConfigId;
    }

    /**
     * Set the SURF configuration number used to process image
     *
     * @param surfConfigId
     */
    public void setSurfConfigId(Integer surfConfigId) {
        this.surfConfigId = surfConfigId;
    }

    /**
     * Return a collection of interest points
     *
     * @return
     */
    public ArrayList<InterestPoint> getPoints() {
        return points;
    }

    /**
     * Set the collection of interest points
     *
     * @param points
     */
    public void setPoints(ArrayList<InterestPoint> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterestPointContainer that = (InterestPointContainer) o;
        return Objects.equals(tileId, that.tileId) &&
                Objects.equals(surfConfigId, that.surfConfigId) &&
                Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tileId, surfConfigId, points);
    }
}
