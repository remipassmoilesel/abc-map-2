package org.abcmap.core.draw;

import java.awt.*;

/**
 * Line styles used to draw
 */
public enum LineStyle {

    LINE_STROKE, SMALL_DASH_STROKE, MEDIUM_DASH_STROKE, LARGE_DASH_STROKE;

    public BasicStroke getSwingStroke(int thickness) {

        if (LINE_STROKE.equals(this)) {
            return new BasicStroke(thickness);
        } else if (SMALL_DASH_STROKE.equals(this)) {
            float d = 5.0f;
            return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, d,
                    new float[]{d}, 0.0f);
        } else if (MEDIUM_DASH_STROKE.equals(this)) {
            float d = 20.0f;
            return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, d,
                    new float[]{d}, 0.0f);
        } else if (LARGE_DASH_STROKE.equals(this)) {
            float d = 40.0f;
            return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, d,
                    new float[]{d}, 0.0f);
        } else
            throw new IllegalAccessError();
    }

    public static LineStyle safeValueOf(String linestyle) {
        try {
            return LineStyle.valueOf(linestyle);
        } catch (IllegalArgumentException | NullPointerException e) {
            return LineStyle.LINE_STROKE;
        }

    }

}
