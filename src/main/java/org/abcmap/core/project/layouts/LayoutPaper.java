package org.abcmap.core.project.layouts;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Path;

/**
 * Utility used when printing layout sheets
 * <p>
 * This class can be added to a Java Book
 */
public class LayoutPaper extends PageFormat implements Printable {

    private static final CustomLogger logger = LogManager.getLogger(LayoutPaper.class);

    private final Path imagePath;
    private final LayoutSheet sheet;
    private SoftReference<BufferedImage> imageSoftRef;

    public LayoutPaper(LayoutSheet sheet, Path tmp, BufferedImage image) {
        this.imagePath = tmp;
        this.imageSoftRef = new SoftReference<>(image);
        this.sheet = sheet;
    }

    /**
     * Return image or null if an error occur
     *
     * @return
     */
    public BufferedImage getImage() {

        // if image is null, read it from disk
        if (imageSoftRef.get() == null) {

            BufferedImage image = null;
            try {
                image = ImageIO.read(imagePath.toFile());
            } catch (IOException e) {
                logger.error(e);
            }

            imageSoftRef = new SoftReference<>(image);
        }

        return imageSoftRef.get();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        Graphics2D g2d = (Graphics2D) graphics;

        g2d.drawImage(getImage(), 0, 0, (int) sheet.getWidthPx(72), (int) sheet.getHeightPx(72), null);

        return Printable.PAGE_EXISTS;
    }

    @Override
    public double getImageableX() {
        return 0;
    }

    @Override
    public double getImageableY() {
        return 0;
    }

    @Override
    public double getWidth() {
        return sheet.getWidthPx(72);
    }

    @Override
    public double getImageableWidth() {
        return sheet.getWidthPx(72);
    }

    @Override
    public double getHeight() {
        return sheet.getHeightPx(72);
    }

    @Override
    public double getImageableHeight() {
        return sheet.getHeightPx(72);
    }


}
