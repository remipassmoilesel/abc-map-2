package org.abcmap.gui.components.dock;

import org.abcmap.gui.ie.InteractionElementGroup;

import javax.swing.JComponent;
import java.util.List;

/**
 * Utility to build docks from groups of interaction elements
 */
public class DockBuilder {

    private List<Object> widgets;
    private DockOrientation orientation;

    public void setWidgets(List<Object> ieg) {
        this.widgets = ieg;
    }

    public void setOrientation(DockOrientation orientation) {
        this.orientation = orientation;
    }

    public Dock make() {

        Dock dock = new Dock(orientation);

        for (Object o : widgets) {

            // create dock menu widget
            if (o instanceof InteractionElementGroup) {

                InteractionElementGroup ieg = (InteractionElementGroup) o;

                DockMenuWidget btt = new DockMenuWidget();
                btt.setInteractionElementGroup(ieg);
                btt.setWindowMode(ieg.getWindowMode());

                dock.addWidget(btt);

                btt.revalidate();
                btt.repaint();
            }

            // add Swing element
            else if (o instanceof JComponent) {
                dock.addWidget((JComponent) o);
            }

            // element not recognized
            else {
                throw new IllegalArgumentException("Unknown type: " + o.getClass().getName());
            }

        }

        return dock;
    }
}
