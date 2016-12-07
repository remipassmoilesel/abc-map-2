package org.abcmap.core.partials;

import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * Rendered partial that can be used to display a map.
 * <p>
 * This object wrap an area and an image. Image reference can be null because images are linked with soft links, in order to free memory when it is necessary.
 */
public class RenderedPartial {

    /**
     * Soft reference to the rendered image
     */
    private SoftReference<BufferedImage> imageSoftRef;

    /**
     * Size in pixel
     */
    private int renderedWidth;

    /**
     * Size in pixel
     */
    private int renderedHeight;

    /**
     * World area of the referenced partial
     */
    private ReferencedEnvelope envelope;

    /**
     * Identifier of layer owner
     */
    private String layerId;


    public RenderedPartial(BufferedImage image, ReferencedEnvelope envelope, int renderedWidth, int renderedHeight, String layerId) {
        setImage(image, renderedWidth, renderedHeight);
        this.envelope = envelope;
        this.layerId = layerId;
    }

    /**
     * Return reference to image if image is yet in memory.
     * <p>
     * If not return null
     *
     * @return
     */
    public BufferedImage getImage() {
        if (imageSoftRef == null) {
            return null;
        }
        return imageSoftRef.get();
    }

    /**
     * Set the rendered image. Image is retained by a soft reference, so it can become null
     * <p>
     * Width and height must be specified because partial can be drawn BEFORE image is ready.
     *
     * @param image
     */
    public void setImage(BufferedImage image, int width, int height) {
        this.imageSoftRef = new SoftReference<>(image);
        this.renderedWidth = width;
        this.renderedHeight = height;
    }

    /**
     * Get world area of the rendered image
     *
     * @return
     */
    public ReferencedEnvelope getEnvelope() {
        return new ReferencedEnvelope(envelope);
    }

    /**
     * Return pixel size of partial
     *
     * @return
     */
    public int getRenderedHeight() {
        return renderedHeight;
    }

    /**
     * Return pixel size of partial
     *
     * @return
     */
    public int getRenderedWidth() {
        return renderedWidth;
    }

    @Override
    public String toString() {
        return "RenderedPartial{" +
                "imageSoftRef=" + imageSoftRef +
                ", renderedWidth=" + renderedWidth +
                ", renderedHeight=" + renderedHeight +
                ", envelope=" + envelope +
                '}';
    }

    /**
     * Return layer identifier to which this partial belong
     *
     * @return
     */
    public String getLayerId() {
        return layerId;
    }

    /**
     * Set layer identifier to which this partial belong
     *
     * @param layerId
     */
    public void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderedPartial that = (RenderedPartial) o;
        return renderedWidth == that.renderedWidth &&
                renderedHeight == that.renderedHeight &&
                Objects.equals(envelope, that.envelope) &&
                Objects.equals(layerId, that.layerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderedWidth, renderedHeight, envelope, layerId);
    }
}
