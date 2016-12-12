package org.abcmap.core.draw;

import org.abcmap.gui.utils.GuiUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Texture which can be rendered in Java element
 */
public class BackgroundTexture {

    private static final String RIGHT = "RIGHT";
    private static final String LEFT = "LEFT";

    private Texture type;
    private Color color;
    private TexturePaint texture;
    private BufferedImage image;

    BackgroundTexture(Texture type, Color color) {

        this.type = type;
        this.color = color;

        this.texture = null;
        this.image = null;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof BackgroundTexture == false)
            return false;

        BackgroundTexture shp = (BackgroundTexture) obj;
        Object[] toCompare1 = new Object[]{this.type, this.color,};
        Object[] toCompare2 = new Object[]{shp.type, shp.color,};
        return Arrays.deepEquals(toCompare1, toCompare2);
    }

    public TexturePaint getPaint() {
        if (texture == null){
            createTexture();
        }
        return texture;
    }

    private void createTexture() {

        if (Texture.HLINES_VERTICAL.equals(type)) {
            createAndDrawVerticals(type.getThickness(), type.getSpace());
        }

        else if (Texture.HLINES_HORIZONTAL.equals(type)) {
            createAndDrawHorizontals(type.getThickness(), type.getSpace());
        }

        else if (Texture.HLINES_OBLIQUE_RIGHT.equals(type)) {
            createAndDrawObliques(type.getThickness(), type.getSpace(), RIGHT);
        }

        else if (Texture.HLINES_OBLIQUE_LEFT.equals(type)) {
            createAndDrawObliques(type.getThickness(), type.getSpace(), LEFT);
        }

        else if (Texture.FILLED_POINTS.equals(type)) {
            createAndDrawPoints(type.getThickness(), type.getSpace(), true);
        }

        else if (Texture.DRAWED_POINTS.equals(type)) {
            createAndDrawPoints(type.getThickness(), type.getSpace(), false);
        } else {
            throw new IllegalStateException("Unknown type: " + type);
        }

        // showTextureBorders();

        texture = new TexturePaint(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()));

    }

    private void createAndDrawPoints(int thickness, int space, boolean fill) {

        int width = thickness + space * 2;
        Graphics2D g = createImageAndGetGraphics(width, width);

        g.setColor(color);
        if (fill){
            g.fillOval(space, space, thickness, thickness);
        }

        else{
            g.drawOval(space, space, thickness, thickness);
        }

    }

    private void createAndDrawObliques(int thickness, int space, String direction) {

        // coté du triangle rectangle d'hypothenuse thickness
        int triSide = (int) (thickness * Math.cos(Math.toRadians(45)));

        // int width = thickness + space * 2;
        int width = (int) (space * Math.cos(Math.toRadians(45)) + triSide * 2);
        Graphics2D g = createImageAndGetGraphics(width, width);

        // ligne principale
        Polygon line = new Polygon(new int[4], new int[4], 4);

        // petite ligne basse
        Polygon line2 = new Polygon(new int[3], new int[3], 3);

        // point pour ligne vers la droite
        if (RIGHT.equals(direction)) {

            // ligne principale
            // coin haut droite
            line.xpoints[0] = triSide;
            line.ypoints[0] = -triSide;

            // coin bas droite
            line.xpoints[1] = width + triSide;
            line.ypoints[1] = width - triSide;

            // coin bas gauche
            line.xpoints[2] = width;
            line.ypoints[2] = width;

            // coin heut gauche
            line.xpoints[3] = 0;
            line.ypoints[3] = 0;

            // petite ligne basse
            // point haut
            line2.xpoints[0] = 0;
            line2.ypoints[0] = width - triSide * 2;

            // point bas droit
            line2.xpoints[1] = triSide * 2;
            line2.ypoints[1] = width;

            // point bas gauche
            line2.xpoints[2] = 0;
            line2.ypoints[2] = width;
        }

        // point pour ligne vers la gauche
        else {

            // ligne principale
            // coin haut gauche
            line.xpoints[0] = width - triSide;
            line.ypoints[0] = -triSide;

            // coin haut droite
            line.xpoints[1] = width;
            line.ypoints[1] = 0;

            // coin bas droite
            line.xpoints[2] = 0;
            line.ypoints[2] = width;

            // coin bas gauche
            line.xpoints[3] = -triSide;
            line.ypoints[3] = width - triSide;

            // petite ligne basse
            // point haut
            line2.xpoints[0] = width;
            line2.ypoints[0] = width - triSide * 2;

            // point bas droit
            line2.xpoints[1] = width;
            line2.ypoints[1] = width;

            // point bas gauche
            line2.xpoints[2] = width - triSide * 2;
            line2.ypoints[2] = width;

        }

        g.setColor(color);
        g.fill(line);

        g.setColor(color);
        g.fill(line2);

    }

    private void createAndDrawHorizontals(int thickness, int space) {

        Graphics2D g = createImageAndGetGraphics(thickness, thickness + space);

        g.setColor(color);
        g.fillRect(0, 0, thickness, thickness);

    }

    private void createAndDrawVerticals(int thickness, int space) {

        Graphics2D g = createImageAndGetGraphics(thickness + space, thickness);

        g.setColor(color);
        g.fillRect(0, 0, thickness, thickness);

    }

    private Graphics2D createImageAndGetGraphics(int width, int height) {

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();

        GuiUtils.applyQualityRenderingHints(g);

        return g;
    }

    /**
     * Show texture with borders for debug purposes
     */
    private void showTextureBorders() {

        Graphics2D g = image.createGraphics();

        GuiUtils.applyQualityRenderingHints(g);

        g.setColor(Color.red);
        g.drawRect(0, 0, image.getWidth(), image.getHeight());

    }

}
