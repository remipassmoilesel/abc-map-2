package org.abcmap.core.rendering.partials;

import org.abcmap.gui.utils.GuiUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * Rendered partial that can be used to display a map.
 * <p>
 * This object wrap an area and an image. Image reference can be null because images are linked with soft links, in order to free memory when it is necessary.
 */
public class RenderedPartial {

    private static long partialCount = 0;
    private static BufferedImage waitingImage;
    private long databaseId;

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

    /**
     * Debug id
     */
    private long debugId;

    /**
     * If image is set to null, a waiting generated image will be used
     *
     * @param image
     * @param envelope
     * @param renderedWidth
     * @param renderedHeight
     * @param layerId
     */
    public RenderedPartial(BufferedImage image, ReferencedEnvelope envelope, int renderedWidth, int renderedHeight, String layerId) {
        partialCount++;
        this.debugId = partialCount;
        this.envelope = envelope;
        this.layerId = layerId;
        this.databaseId = -1;
        setImage(image, renderedWidth, renderedHeight);
    }

    /**
     * Return reference to image if image is yet in memory.
     * <p>
     * If not return null
     *
     * @return
     */
    public BufferedImage getImage() {

        if (imageSoftRef != null) {
            return imageSoftRef.get();
        }

        return null;
    }

    /**
     * Set the rendered image. Image is retained by a soft reference, so it can become null
     * <p>
     * Width and height must be specified because partial can be drawn BEFORE image is ready.
     *
     * @param image
     */
    public void setImage(BufferedImage image, int width, int height) {

        this.renderedWidth = width;
        this.renderedHeight = height;

        if (image == null) {
            image = getWaitingImage();
        }

        this.imageSoftRef = new SoftReference<>(image);
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
        return Objects.equals(envelope, that.envelope) &&
                Objects.equals(layerId, that.layerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(envelope, layerId);
    }

    /**
     * Get debug unique ID of partial. This id is not a database ID, and should not be used for other task than debugging
     *
     * @return
     */
    public long getDebugId() {
        return debugId;
    }

    @Override
    public String toString() {
        return "RenderedPartial{" + this.hashCode() + ", " +
                "imageSoftRef=" + imageSoftRef +
                ", renderedWidth=" + renderedWidth +
                ", renderedHeight=" + renderedHeight +
                ", envelope=" + envelope +
                '}';
    }

    /**
     * Return true if specified partial is loaded
     *
     * @param part
     * @return
     */
    public static boolean isLoaded(RenderedPartial part) {

        if (part == null) {
            return false;
        }

        if (part.getImage() == null) {
            return false;
        }

        if (part.getImage() == waitingImage) {
            return false;
        }

        return true;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    /**
     * Get a waiting image, an image to display while partial is rendering
     *
     * @return
     */
    public BufferedImage getWaitingImage() {

        if (waitingImage == null) {

            int side = renderedWidth;
            waitingImage = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = (Graphics2D) waitingImage.getGraphics();
            Graphics2D g2dT = (Graphics2D) waitingImage.createGraphics();

            int w = waitingImage.getWidth();
            int h = waitingImage.getHeight();

            // no transparency here, because the are possibly several layers
            //g2d.setComposite(GuiUtils.createTransparencyComposite(0.3f));

            g2dT.setComposite(GuiUtils.createTransparencyComposite(0.0f));
            g2dT.setColor(Color.white);
            g2dT.fillRect(0, 0, w, h);

            // draw a rectangle
            int thick = 3;
            g2d.setColor(new Color(0xF0EEEE));
            g2d.setStroke(new BasicStroke(thick));
            g2d.drawRect(0 + thick, 0 + thick, w - thick, h - thick);

            // draw tree points in rectangle
            g2d.setColor(Color.blue);
            g2d.setFont(new Font("Dialog", Font.BOLD, 50));
            g2d.drawString("...", w / 2 - 20, h / 2);
        }

        return waitingImage;
    }
}
