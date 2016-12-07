package org.abcmap.core.partials;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.dao.DataModel;
import org.abcmap.core.utils.BufferedImagePersister;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Portion of rendered map, serializable version. Contains:
 */
@DatabaseTable(tableName = SerializableRenderedPartial.TABLE_NAME)
public class SerializableRenderedPartial implements DataModel {

    public static final String TABLE_NAME = ConfigurationConstants.SQL_TABLE_PREFIX + "PARTIALS";
    public static final String PARTIAL_ID_FIELD_NAME = "ID";
    public static final String PARTIAL_IMAGE_FIELD_NAME = "IMAGE";
    public static final String PARTIAL_X1_FIELD_NAME = "X1";
    public static final String PARTIAL_X2_FIELD_NAME = "X2";
    public static final String PARTIAL_Y1_FIELD_NAME = "Y1";
    public static final String PARTIAL_Y2_FIELD_NAME = "Y2";
    public static final String PARTIAL_CRS_FIELD_NAME = "CRS";
    public static final String PARTIAL_LAYERID_FIELD_NAME = "LAYERID";


    @DatabaseField(generatedId = true, columnName = PARTIAL_ID_FIELD_NAME)
    private long id;

    /**
     * Hard reference to the rendered image. This reference is used only when writing object to database
     */
    @DatabaseField(columnName = PARTIAL_IMAGE_FIELD_NAME, persisterClass = BufferedImagePersister.class)
    private BufferedImage image;

    /**
     * World unit, from bottom left corner
     */
    @DatabaseField(columnName = PARTIAL_X1_FIELD_NAME)
    private double x1;

    /**
     * World unit, from bottom left corner
     */
    @DatabaseField(columnName = PARTIAL_X2_FIELD_NAME)
    private double x2;

    /**
     * World unit, from bottom left corner
     */
    @DatabaseField(columnName = PARTIAL_Y1_FIELD_NAME)
    private double y1;

    /**
     * World unit, from bottom left corner
     */
    @DatabaseField(columnName = PARTIAL_Y2_FIELD_NAME)
    private double y2;

    /**
     * Identifier of layer
     */
    @DatabaseField(columnName = PARTIAL_LAYERID_FIELD_NAME)
    private String layerId;

    /**
     * Identifier of Coordinate Reference System
     */
    @DatabaseField(columnName = PARTIAL_CRS_FIELD_NAME)
    private String crsId;

    public SerializableRenderedPartial() {

    }

    public SerializableRenderedPartial(RenderedPartial part) {
        this(part.getImage(), part.getEnvelope(), part.getLayerId());
    }

    public SerializableRenderedPartial(BufferedImage img, ReferencedEnvelope ev, String layerId) {
        this(img, ev.getMinX(), ev.getMaxX(), ev.getMinY(), ev.getMaxY(), crsToId(ev.getCoordinateReferenceSystem()), layerId);
    }

    public SerializableRenderedPartial(BufferedImage img, double x1, double x2, double y1, double y2, String crsId, String layerId) {
        this.image = img;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.crsId = crsId;
        this.layerId = layerId;
    }

    public void setImage(BufferedImage img) {
        this.image = img;
    }

    public BufferedImage getImage() {
        return image;
    }

    public static String crsToId(CoordinateReferenceSystem crs) {
        String authority = crs.getName().getAuthority() != null ? crs.getName().getAuthority() + ":" : "";
        return authority + crs.getName().getCode();
    }

    public static CoordinateReferenceSystem idToCrs(String crsId) throws FactoryException {
        return CRS.decode(crsId);
    }

    public String getCrsId() {
        return crsId;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializableRenderedPartial that = (SerializableRenderedPartial) o;
        return Double.compare(that.x1, x1) == 0 &&
                Double.compare(that.x2, x2) == 0 &&
                Double.compare(that.y1, y1) == 0 &&
                Double.compare(that.y2, y2) == 0 &&
                Objects.equals(crsId, that.crsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, x2, y1, y2, crsId);
    }

    @Override
    public String toString() {
        return "SerializableRenderedPartial{" +
                "image=" + image +
                ", x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", crs=" + crsId +
                '}';
    }
}
