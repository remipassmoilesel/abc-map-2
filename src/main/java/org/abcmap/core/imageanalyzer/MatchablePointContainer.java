package org.abcmap.core.imageanalyzer;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.project.dao.DataModel;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Object containing matchable points for a image
 */
@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "IMAGE_POINTS")
public class MatchablePointContainer implements DataModel {

    private static final String IMAGE_ID_FIELD_NAME = "IMAGE_ID";
    private static final String SURF_MODE_FIELD_NAME = "SURF_MODE";
    private static final String POINTS_FIELD_NAME = "POINTS";

    @DatabaseField(id = true, columnName = IMAGE_ID_FIELD_NAME)
    private String imageId;

    @DatabaseField(columnName = SURF_MODE_FIELD_NAME)
    private Integer surfMode;

    @DatabaseField(columnName = POINTS_FIELD_NAME, dataType = DataType.SERIALIZABLE)
    private ArrayList<MatchablePoint> points;

    public MatchablePointContainer() {
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String tileId) {
        this.imageId = tileId;
    }

    public Integer getSurfMode() {
        return surfMode;
    }

    public void setSurfMode(Integer surfMode) {
        this.surfMode = surfMode;
    }

    public ArrayList<MatchablePoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<MatchablePoint> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchablePointContainer that = (MatchablePointContainer) o;
        return Objects.equals(imageId, that.imageId) &&
                Objects.equals(surfMode, that.surfMode) &&
                Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, surfMode, points);
    }
}
