package org.abcmap.core.project.tiles;

import com.vividsolutions.jts.geom.Coordinate;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 23/11/16.
 */
public class TileContainer {

    private Coordinate position;
    private BufferedImage image;
    private String tileId;

    public TileContainer(String tileId, BufferedImage image, Coordinate position) {
        this.image = image;
        this.tileId = tileId;
        this.position = position;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getTileId() {
        return tileId;
    }

    public void setTileId(String tileId) {
        this.tileId = tileId;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "TileContainer{" +
                "position=" + position +
                ", image=" + image +
                ", tileId='" + tileId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileContainer that = (TileContainer) o;
        return Objects.equals(position, that.position) &&
                Objects.equals(image, that.image) &&
                Objects.equals(tileId, that.tileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, image, tileId);
    }
}
