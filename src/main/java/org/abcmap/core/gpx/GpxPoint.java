package org.abcmap.core.gpx;

import com.vividsolutions.jts.geom.Coordinate;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 01/02/17.
 */
public class GpxPoint {

    private String date;
    private long lat;
    private long lon;
    private long ele;

    public GpxPoint(BigDecimal lat, BigDecimal lon, BigDecimal ele) {
        this.lat = lat.longValue();
        this.lon = lon.longValue();
        this.ele = ele.longValue();
    }

    public GpxPoint(BigDecimal lat, BigDecimal lon, BigDecimal ele, String date) {
        this.lat = lat.longValue();
        this.lon = lon.longValue();
        this.ele = ele.longValue();
        this.date = date;
    }

    public Coordinate getCoordinate() {
        return new Coordinate(lat, lon);
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public long getEle() {
        return ele;
    }

    public void setEle(long ele) {
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
