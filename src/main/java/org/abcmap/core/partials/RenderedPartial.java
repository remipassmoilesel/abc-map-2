package org.abcmap.core.partials;

import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

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

    public RenderedPartial(BufferedImage image, ReferencedEnvelope envelope, int renderedWidth, int renderedHeight) {
        setImage(image, renderedWidth, renderedHeight);
        this.envelope = envelope;
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

    public int getRenderedHeight() {
        return renderedHeight;
    }

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
}
