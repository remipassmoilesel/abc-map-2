package org.abcmap.core.project.tiles;

import com.vividsolutions.jts.geom.Coordinate;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Container to manipulate tiles with image and few informations:
 * <p>
 * - ID: string id
 * <p>
 * - position: coordinate in map unit, placed by bottom left corner
 * <p>
 * - image: buffered image of tile
 */
public class TileContainer {

    private Coordinate position;
    private BufferedImage image;
    private String tileId;

    public TileContainer(String tileId, BufferedImage image, Coordinate position) {

        if (tileId == null) {
            this.tileId = TileStorage.generateTileId();
        } else {
            this.tileId = tileId;
        }

        this.image = image;
        this.position = position;
    }

    /**
     * Get the buffered image of tile
     *
     * @return
     */
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Get unique identifier of tile
     *
     * @return
     */
    public String getTileId() {
        return tileId;
    }

    public void setTileId(String tileId) {
        this.tileId = tileId;
    }

    /**
     * Get position of bottom left corner of tile on map
     *
     * @return
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Set position of bottom left corner of tile on map
     *
     * @return
     */
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
