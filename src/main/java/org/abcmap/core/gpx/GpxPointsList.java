package org.abcmap.core.gpx;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Generic container used to wrap GPX data through format versions.
 * <p>
 * Created by remipassmoilesel on 01/02/17.
 */
public class GpxPointsList {

    public enum Type{
        WAY_POINT,
        TRACK,
        ROUTE,
    }

    private final Type type;
    private ArrayList<GpxPoint> points;

    public GpxPointsList(Type type) {
        this.type = type;
        this.points = new ArrayList<>();
    }

    public Type getType() {
        return type;
    }

    public ArrayList<GpxPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<GpxPoint> points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GpxPointsList that = (GpxPointsList) o;
        return type == that.type &&
                Objects.equals(points, that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, points);
    }

    @Override
    public String toString() {
        return "GpxPointsList{" +
                "type=" + type +
                ", points=" + points +
                '}';
    }
}
