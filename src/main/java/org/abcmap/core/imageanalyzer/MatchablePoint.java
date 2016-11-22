package org.abcmap.core.imageanalyzer;

import java.io.Serializable;
import java.util.Objects;

/**
 * Custom serializable representation of interest point
 */
public class MatchablePoint implements Serializable {

    private static final long serialVersionUID = 2670622864656743798L;

    private Double y;
    private Double x;

    public MatchablePoint(Double x, Double y){
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchablePoint that = (MatchablePoint) o;
        return Objects.equals(y, that.y) &&
                Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }

}
