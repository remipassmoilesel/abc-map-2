package org.abcmap.core.tiles;

import org.geotools.geometry.jts.ReferencedEnvelope;

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

    /**
     * Area this tile fill on the world
     */
    private ReferencedEnvelope area;

    /**
     * Image of tile
     */
    private BufferedImage image;

    /**
     * Unique id of tile
     */
    private String tileId;

    public TileContainer(BufferedImage image, ReferencedEnvelope area) {
        this(null, image, area);
    }

    public TileContainer(String tileId, BufferedImage image, ReferencedEnvelope area) {

        if (tileId == null) {
            this.tileId = TileStorage.generateTileId();
        } else {
            this.tileId = tileId;
        }

        this.image = image;
        this.area = area;
    }

    /**
     * Get the buffered image of tile
     *
     * @return
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Set image of tile
     *
     * @param image
     */
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

    /**
     * Set unique id of tile
     *
     * @param tileId
     */
    public void setTileId(String tileId) {
        this.tileId = tileId;
    }

    /**
     * Get area covered by this tile on world
     *
     * @return
     */
    public ReferencedEnvelope getArea() {
        return area;
    }

    /**
     * Set area covered by this tile on world
     *
     * @param area
     */
    public void setArea(ReferencedEnvelope area) {
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileContainer that = (TileContainer) o;
        return Objects.equals(area, that.area) &&
                Objects.equals(image, that.image) &&
                Objects.equals(tileId, that.tileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, image, tileId);
    }

    @Override
    public String toString() {
        return "TileContainer{" +
                "area=" + area +
                ", image=" + image +
                ", tileId='" + tileId + '\'' +
                '}';
    }

}
