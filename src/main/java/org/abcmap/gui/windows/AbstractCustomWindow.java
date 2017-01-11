package org.abcmap.gui.windows;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.utils.GraphicsConsumer;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

public abstract class AbstractCustomWindow extends JFrame {

    private static final CustomLogger logger = LogManager.getLogger(AbstractCustomWindow.class);

    /**
     * Painters that can be registered to paint over main window
     */
    private final ArrayList<GraphicsConsumer> painters;

    private GuiManager guim;
    private ArrayList<Rectangle> shapesToHighlight;
    private GraphicsConsumer veilPainter;

    public AbstractCustomWindow() {

        setTitle("");

        guim = Main.getGuiManager();
        guim.setWindowIconFor(this);

        shapesToHighlight = new ArrayList<>();
        painters = new ArrayList<>();

        CustomGlassPane glassPane = new CustomGlassPane();
        setGlassPane(glassPane);

    }

    /**
     * Let painters paint over window if needed
     */
    private class CustomGlassPane extends JPanel {

        CustomGlassPane() {
            super();
            setOpaque(false);
            setBorder(null);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            if (painters.size() > 0) {
                for (GraphicsConsumer con : painters) {
                    try {
                        con.paint(g2d);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        }
    }

    /**
     * Register an object which can paint over this window
     *
     * @param consumer
     */
    public void addPainter(GraphicsConsumer consumer) {

        if (painters.contains(consumer) == true) {
            logger.warning("Painter already present, it will be not added a second time");
        }

        // painter not present, add it
        else {
            painters.add(consumer);
        }

        if (painters.size() > 0) {
            Component glass = getGlassPane();
            glass.setVisible(true);
            glass.revalidate();
            glass.repaint();
            revalidate();
            repaint();
        }
    }

    /**
     * Remove a painter
     *
     * @param consumer
     */
    public void removePainter(GraphicsConsumer consumer) {
        painters.remove(consumer);

        if (painters.size() < 1) {
            getGlassPane().setVisible(false);
        }
    }

    /**
     * Remove all registered painters
     */
    public void clearAllPainters() {
        painters.clear();
    }

    /**
     * Draw a veil on window around specified shape(s)
     * <p>
     * Coordinates should be screen coordinates
     *
     * @param
     */
    public void highlightRectangle(Point locationOnScreen, int width, int height) {
        highlightRectangle(new Rectangle(locationOnScreen.x, locationOnScreen.y, width, height));
    }

    /**
     * Draw a veil on window around specified shape(s)
     * <p>
     * Coordinates should be screen coordinates
     *
     * @param screenRectangle
     */
    public void highlightRectangle(Rectangle screenRectangle) {

        // add shape in list of shape to paint
        shapesToHighlight.add(screenRectangle);

        // create painter if needed
        if (veilPainter == null) {
            veilPainter = (g2d) -> {

                GuiUtils.applyQualityRenderingHints(g2d);

                Rectangle bounds = g2d.getClipBounds();

                // create a transform to translate shapes from screen coord space to glass pane coord space
                Point loc = getGlassPane().getLocationOnScreen();
                AffineTransform trans = AffineTransform.getTranslateInstance(-loc.x, -loc.y);

                // create area to fill, by subtracting all shapes to highlight
                Area toFill = new Area(bounds);
                for (Rectangle r : new ArrayList<>(shapesToHighlight)) {
                    Area toSub = new Area(r);
                    toSub.transform(trans);
                    toFill.subtract(toSub);
                }

                // fill with a gray veil
                Graphics2D g2dT = (Graphics2D) g2d.create();
                g2dT.setComposite(AlphaComposite.SrcOver.derive(0.8f));
                g2dT.setColor(Color.black);
                g2dT.fill(toFill);

            };
        }

        // add painter
        if (painters.contains(veilPainter) == false) {
            addPainter(veilPainter);
        }


    }

    /**
     * Remove veil around specified shape(s)
     * <p>
     * Coordinates should be screen coordinates
     *
     * @param screenRectangle
     */
    public void unhighlightRectangle(Rectangle screenRectangle) {

        // we can not use this method here, because Area.equals != Object.equals()
        boolean removed = shapesToHighlight.remove(screenRectangle);

        // remove painter is there is nothing more
        if (shapesToHighlight.size() < 1) {
            removePainter(veilPainter);
        }

        // alert if nothing have been removed
        if (removed == false) {
            logger.warning("Shape was not removed: " + screenRectangle);
        }

    }


    /**
     * Set title of window, with software name as prefix
     *
     * @param arg0
     */
    @Override
    public void setTitle(String arg0) {
        if (arg0.isEmpty() == false) {
            arg0 = " - " + arg0;
        }
        super.setTitle(ConfigurationConstants.SOFTWARE_NAME + arg0);
    }

}
