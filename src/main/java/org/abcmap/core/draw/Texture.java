package org.abcmap.core.draw;

/**
 * Textures applied to shapes
 */
public enum Texture {

    PLAIN,

    HLINES_VERTICAL,

    HLINES_HORIZONTAL,

    HLINES_OBLIQUE_RIGHT,

    HLINES_OBLIQUE_LEFT,

    FILLED_POINTS(8, 4),

    DRAWED_POINTS(8, 4);

    private int space;
    private int thickness;

    private Texture(int thickness, int space) {
        this.thickness = thickness;
        this.space = space;
    }

    private Texture() {
        this(8, 8);
    }

    public int getSpace() {
        return space;
    }

    public int getThickness() {
        return thickness;
    }

    public static Texture safeValueOf(String texture) {
        try {
            return Texture.valueOf(texture);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Texture.PLAIN;
        }
    }

}
