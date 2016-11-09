package org.abcmap.core.project;

public enum PMConstants {

    /**
     * Project title
     */
    TITLE,
    /**
     * Project comments
     */
    COMMENT,
    /**
     * Creation date
     */
    CREATED,
    /**
     * Backgrund color
     */
    BG_COLOR,
    /**
     * Layout frames opacity over map when printing
     */
    LAYOUT_FRAME_OPACITY,
    /**
     * Layout frames thicknes when printing
     */
    LAYOUT_FRAME_THICKNESS,

    /**
     * Color used when printing to display layout frames
     */
    LAYOUT_FRAME_COLOR_1,

    /**
     * Color used when printing to display layout frames
     */
    LAYOUT_FRAME_COLOR_2;

    public static PMConstants safeValueOf(String name) {
        try {
            return PMConstants.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}