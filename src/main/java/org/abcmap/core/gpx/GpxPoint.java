package org.abcmap.core.gpx;

import com.vividsolutions.jts.geom.Coordinate;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 01/02/17.
 */
public class GpxPoint {

    private String date;
    private double lat;
    private double lon;
    private double ele;

    public GpxPoint(BigDecimal lat, BigDecimal lon, BigDecimal ele) {
        this.lat = lat.doubleValue();
        this.lon = lon.doubleValue();
        this.ele = ele.doubleValue();
    }

    public GpxPoint(BigDecimal lat, BigDecimal lon, BigDecimal ele, String date) {
        this.lat = lat.doubleValue();
        this.lon = lon.doubleValue();
        this.ele = ele.doubleValue();
        this.date = date;
    }

    public Coordinate getCoordinatePoint() {
        return new Coordinate(lon, lat);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getEle() {
        return ele;
    }

    public void setEle(double ele) {
        this.ele = ele;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GpxPoint gpxPoint = (GpxPoint) o;
        return lat == gpxPoint.lat &&
                lon == gpxPoint.lon &&
                ele == gpxPoint.ele &&
                Objects.equals(date, gpxPoint.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, lat, lon, ele);
    }

    @Override
    public String toString() {
        return "GpxPoint{" +
                "date='" + date + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", ele=" + ele +
                '}';
    }
}
