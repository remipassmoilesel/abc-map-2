package org.abcmap.core.project.layouts;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Objects;

/**
 * Represent a sheet where map an be drawn at various scale and position
 * <p>
 * This object is designed to be serialized in projects
 */
@DatabaseTable(tableName = ConfigurationConstants.SQL_TABLE_PREFIX + "LAYOUTS")
public class LayoutSheet implements DataModel {

    private static final CustomLogger logger = LogManager.getLogger(LayoutSheet.class);

    private static final String ID_FIELD_NAME = "ID";
    private static final String IS_INDEX_FIELD_NAME = "IS_INDEX";
    private static final String ENV_MIN_X_FIELD_NAME = "ENV_MIN_X";
    private static final String ENV_MIN_Y_FIELD_NAME = "ENV_MIN_Y";
    private static final String ENV_MAX_X_FIELD_NAME = "ENV_MAX_X";
    private static final String ENV_MAX_Y_FIELD_NAME = "ENV_MAX_Y";

    private static final String WIDTH_MM_FIELD_NAME = "WIDTH_MM";
    private static final String HEIGHT_MM_FIELD_NAME = "HEIGHT_MM";

    private static final String NUMBER_FIELD_NAME = "NUMBER";

    private static final String CRS_FIELD_NAME = "CRS";

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField(columnName = IS_INDEX_FIELD_NAME)
    private boolean index;

    @DatabaseField(columnName = ENV_MIN_X_FIELD_NAME)
    private double minx;

    @DatabaseField(columnName = ENV_MIN_Y_FIELD_NAME)
    private double miny;

    @DatabaseField(columnName = ENV_MAX_X_FIELD_NAME)
    private double maxx;

    @DatabaseField(columnName = ENV_MAX_Y_FIELD_NAME)
    private double maxy;

    @DatabaseField(columnName = WIDTH_MM_FIELD_NAME)
    private double widthMm;

    @DatabaseField(columnName = HEIGHT_MM_FIELD_NAME)
    private double heightMm;

    @DatabaseField(columnName = NUMBER_FIELD_NAME)
    private int number;

    @DatabaseField(columnName = CRS_FIELD_NAME)
    private String crsId;

    public LayoutSheet() {
    }

    public LayoutSheet(boolean index, double minx, double miny, double maxx, double maxy, double widthMm, double heightMm, int number, double scale,
                       CoordinateReferenceSystem crs) {
        this.index = index;
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.number = number;
        this.crsId = GeoUtils.crsToString(crs);
    }

    /**
     * Return true if this sheet is an index sheet, if others sheet are painted on map represented in this sheet
     *
     * @return
     */
    public boolean isIndex() {
        return index;
    }

    /**
     * If set to true, this sheet will paint other sheets envelope in order to show it to users
     *
     * @param index
     */
    public void setIndex(boolean index) {
        this.index = index;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public double getMinx() {
        return minx;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public void setMinx(double minx) {
        this.minx = minx;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public double getMiny() {
        return miny;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public void setMiny(double miny) {
        this.miny = miny;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public double getMaxx() {
        return maxx;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public void setMaxx(double maxx) {
        this.maxx = maxx;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public double getMaxy() {
        return maxy;
    }

    /**
     * Get coordinate of sheet envelope
     * <p>
     * Values use lower left corner as reference
     *
     * @return
     */
    public void setMaxy(double maxy) {
        this.maxy = maxy;
    }

    /**
     * Get width of representation in millimeters
     * <p>
     *
     * @return
     */
    public double getWidthMm() {
        return widthMm;
    }

    /**
     * Get width converted in pixel at specified resolution
     *
     * @param dpi
     * @return
     */
    public double getWidthPx(double dpi) {
        return Utils.millimeterToPixel(getWidthMm(), dpi);
    }


    /**
     * Get height converted in pixel at specified resolution
     *
     * @param dpi
     * @return
     */
    public double getHeightPx(double dpi) {
        return Utils.millimeterToPixel(getHeightMm(), dpi);
    }

    /**
     * Set width of representation in millimeters
     * <p>
     *
     * @return
     */
    public void setWidthMm(int widthMm) {
        this.widthMm = widthMm;
    }

    /**
     * Get height of representation in millimeters
     * <p>
     *
     * @return
     */
    public double getHeightMm() {
        return heightMm;
    }


    /**
     * Set height of representation in millimeters
     * <p>
     *
     * @return
     */
    public void setHeightMm(int heightMm) {
        this.heightMm = heightMm;
    }

    /**
     * Get number of sheet. First is 0.
     *
     * @return
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set number of sheet. First is 0.
     *
     * @return
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get envelope from current dimensions
     *
     * @return
     */
    public ReferencedEnvelope getEnvelope() {

        CoordinateReferenceSystem crs = null;

        if (crsId != null) {
            try {
                crs = GeoUtils.stringToCrs(crsId);
            } catch (FactoryException e) {
                logger.error(e);
            }
        }

        return new ReferencedEnvelope(minx, maxx, miny, maxy, crs);
    }

    public void setEnvelope(ReferencedEnvelope env) {
        minx = env.getMinX();
        maxx = env.getMaxX();
        miny = env.getMinY();
        maxy = env.getMaxY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayoutSheet that = (LayoutSheet) o;
        return id == that.id &&
                index == that.index &&
                Double.compare(that.minx, minx) == 0 &&
                Double.compare(that.miny, miny) == 0 &&
                Double.compare(that.maxx, maxx) == 0 &&
                Double.compare(that.maxy, maxy) == 0 &&
                Double.compare(that.widthMm, widthMm) == 0 &&
                Double.compare(that.heightMm, heightMm) == 0 &&
                number == that.number &&
                Objects.equals(crsId, that.crsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, index, minx, miny, maxx, maxy, widthMm, heightMm, number, crsId);
    }

    @Override
    public String toString() {
        return "LayoutSheet{" +
                "id=" + id +
                ", index=" + index +
                ", minx=" + minx +
                ", miny=" + miny +
                ", maxx=" + maxx +
                ", maxy=" + maxy +
                ", widthMm=" + widthMm +
                ", heightMm=" + heightMm +
                ", number=" + number +
                ", crsId='" + crsId + '\'' +
                '}';
    }
}
