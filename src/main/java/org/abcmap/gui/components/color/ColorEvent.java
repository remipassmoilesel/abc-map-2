package org.abcmap.gui.components.color;

import java.awt.*;

/**
 * Event used when a color is chosen by user
 */
public class ColorEvent {

    private Color color;
    private long when;
    private Object source;

    public ColorEvent(Color color, Object source) {

        this.color = color;
        this.when = System.currentTimeMillis();
        this.source = source;
    }

    /**
     * Get chosen color
     *
     * @return
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get System.currentTimeMillis(); of event creation
     *
     * @return
     */
    public long getWhen() {
        return when;
    }

    /**
     * Get source component of event
     *
     * @return
     */
    public Object getSource() {
        return source;
    }
}
