package org.abcmap.core.importation.gpx;

import com.vividsolutions.jts.geom.Coordinate;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 01/02/17.
 */
public class GpxPoint {

    private String description;
    private String time;
    private double latitude;
    private double longitude;
    private double elevation;

    public GpxPoint(BigDecimal lat, BigDecimal lon) {
        this(lat, lon, null, null, null);
    }

    public GpxPoint(BigDecimal lat, BigDecimal lon, BigDecimal ele, String date, String desc) {

        // only these two parameters are mandatory
        this.latitude = lat.doubleValue();
        this.longitude = lon.doubleValue();

        // optional parameters
        this.elevation = ele != null ? ele.doubleValue() : null;
        this.description = desc;
        this.time = date;
    }

    public Coordinate getCoordinatePoint() {
        return new Coordinate(longitude, latitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GpxPoint gpxPoint = (GpxPoint) o;
        return Double.compare(gpxPoint.latitude, latitude) == 0 &&
                Double.compare(gpxPoint.longitude, longitude) == 0 &&
                Double.compare(gpxPoint.elevation, elevation) == 0 &&
                Objects.equals(description, gpxPoint.description) &&
                Objects.equals(time, gpxPoint.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, time, latitude, longitude, elevation);
    }
}
