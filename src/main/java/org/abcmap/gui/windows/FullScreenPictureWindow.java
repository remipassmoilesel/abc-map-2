package org.abcmap.gui.windows;

import org.abcmap.core.utils.Utils;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Show a picture in a fullscreen window.
 * <p>
 * Adapt picture size in consequence
 */
public class FullScreenPictureWindow extends AbstractCustomWindow {

    protected boolean debug = false;

    /**
     * Dimensions of display
     */
    protected Dimension displayDim;

    /**
     * Originale dimensions of image
     */
    protected Dimension imageDim;

    /**
     * Screen dimensions
     */
    protected Dimension screenDim;

    /**
     * Image to display
     */
    protected BufferedImage image;

    /**
     * Background around picture
     */
    protected Color backgroundColor;

    /**
     * Where image is drawn
     */
    protected ImagePane imagePane;

    public FullScreenPictureWindow() {

        GuiUtils.throwIfNotOnEDT();

        backgroundColor = Color.darkGray;

        this.screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation(0, 0);
        this.setSize(screenDim);

        this.setUndecorated(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(backgroundColor);
        setContentPane(contentPane);

        this.imagePane = new ImagePane();
        imagePane.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));

        // center pane
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        gridbag.setConstraints(contentPane, constraints);
        contentPane.setLayout(gridbag);

        contentPane.add(imagePane);

    }

    /**
     * Set image to paint
     *
     * @param img
     */
    public void setImage(BufferedImage img) {

        imageDim = new Dimension(img.getWidth(), img.getHeight());

        displayDim = new Dimension();

        // If picture is too large, scale it and keep only shrink one
        if (imageDim.width > screenDim.width || imageDim.height > screenDim.height) {
            this.image = Utils.scaleImage(img, screenDim.width, screenDim.height);
        }

        // else keep original
        else {
            this.image = img;
        }


        displayDim.width = this.image.getWidth();
        displayDim.height = this.image.getHeight();

        refreshImagePane();

        revalidate();
        repaint();
    }

    public void refreshImagePane() {
        imagePane.revalidate();
        imagePane.repaint();
    }

    /**
     * Get display scale to translate distance or points
     *
     * @return
     */
    public float getDisplayScale() {
        return (float) (displayDim.getWidth() / imageDim.getWidth());
    }

    /**
     * Transform rectangles from screen coordinate space to image coordinate space
     *
     * @param r
     * @return
     */
    public Rectangle transformToImageSpace(Rectangle r) {

        Rectangle rect = new Rectangle(r);

        float scale = getDisplayScale();

        rect.x = Math.round(rect.x / scale);
        rect.y = Math.round(rect.y / scale);
        rect.width = Math.round(rect.width / scale);
        rect.height = Math.round(rect.height / scale);

        return rect;
    }

    /**
     * Transform rectangles from image coordinate space to screen coordinate space
     *
     * @param r
     * @return
     */
    public Rectangle transformToScreenSpace(Rectangle r) {

        Rectangle rect = new Rectangle(r);

        float scale = getDisplayScale();

        rect.x = Math.round(rect.x * scale);
        rect.y = Math.round(rect.y * scale);
        rect.width = Math.round(rect.width * scale);
        rect.height = Math.round(rect.height * scale);

        return rect;
    }

    /**
     * Paint image to show. Override this method to modify painting.
     *
     * @param g2d
     */
    protected void paintImagePane(Graphics2D g2d) {
        GuiUtils.applyQualityRenderingHints(g2d);
        g2d.drawImage(image, 0, 0, displayDim.width, displayDim.height, null);
    }

    /**
     * Panel where image is displayed
     */
    protected class ImagePane extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            paintImagePane(g2d);
        }

        @Override
        public Dimension getPreferredSize() {
            return displayDim;
        }

        @Override
        public Dimension getMaximumSize() {
            return displayDim;
        }

        @Override
        public Dimension getMinimumSize() {
            return displayDim;
        }
    }

}
